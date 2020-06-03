package gr.aueb.ds.music.android.lalapp.request;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import gr.aueb.ds.music.framework.error.PublisherNotFoundException;
import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.model.network.Connection;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class ConsumerImplementation extends NodeAbstractImplementation implements Consumer {

    protected transient Connection connection;
    protected NodeDetails connectedBrokerDetails;
    public ArtistName artistName;

    public List<MusicFile> musicFiles;

    public ConsumerImplementation() {
        super();
        this.nodeDetails = new NodeDetails();
    }

    public ConsumerImplementation(Map<String, ?> applicationSettings) {
        super(applicationSettings);
        this.nodeDetails = new NodeDetails();
    }

    public ConsumerImplementation(String name, Map<String, ?> applicationSettings) {
        this(applicationSettings);
        this.nodeDetails.setName(name);
    }

    @Override
    public void register(NodeDetails connectedBrokerDetails, ArtistName artistName) {
        try {
            // If Appropriate Broker is Master continue the communication
            if (!isAppropriateBrokerMaster()) {
                this.connection.close();
                this.connection = new Connection(NetworkHelper.initConnection(connectedBrokerDetails.getIpAddress(), connectedBrokerDetails.getPort()));
            }

            // The following requests is not for Broker Discovery
            this.artistName = new ArtistName(this.artistName.getArtistName(), false);
        } catch (IOException ex) {
            // TODO -- Error Handling & Logging
//            LogHelper.error(this, String.format(PropertiesHelper.getProperty("consumer.register.broker.error"), broker.getNodeDetails().getName()));
        }
    }

    @Override
    public void disconnect(Broker broker, ArtistName artistName) {

    }

    @Override
    public void playData(ArtistName artistName, Value value) {
//        this.downloadChunks(value);
    }

    @Override
    public void init() {
        // Make the Connection
        this.connect();

        // Register to Broker
        this.register(this.connectedBrokerDetails, this.artistName);

        try {
            this.musicFiles = this.getTrackResults();
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "init", ex);
        }
    }

    @Override
    public void connect() {
        this.findAppropriateBroker();
    }

    @Override
    public void disconnect() {

    }

    public MusicFile getMusicFileChunk(Value value, int chunkNo) {
        MusicFile musicFile = null;

        try {
            if (chunkNo == 1) {
                // Do Request and Retrieve Chunks one by one
                musicFile = NetworkHelper.doObjectRequest(this.connection, value);
            }
            else {
                musicFile = (MusicFile) this.connection.getIs().readObject();
            }

            if (musicFile != null) musicFile.setTrackName(musicFile.getTrackName().concat("_chunk" + chunkNo));
        } catch (IOException | ClassNotFoundException ex) {
            // TODO
        }

        return musicFile;
    }

    private MusicFile mergeChunks(Value value, List<byte[]> musicFileChunks) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        MusicFile wholeFile = new MusicFile(value.getMusicFile());
        try {
            for (byte[] bytes : musicFileChunks) {
                byteArrayOutputStream.write(bytes);
            }

            wholeFile.setMusicFileExtract(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new Exception("");
//            throw new FileChunksProcessingException("consumer.merge.file.chunks");
        }

        return wholeFile;
    }

    private void findAppropriateBroker() {
        try {
            AbstractMap.SimpleEntry<String, Integer> masterBrokerIpPort = this.getMasterBrokerIpPort();
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(masterBrokerIpPort.getKey(), masterBrokerIpPort.getValue()), 10000);
            this.connection = new Connection(socket);

            // Get appropriate broker
            this.connectedBrokerDetails = NetworkHelper.doObjectRequest(this.connection, this.artistName);
        } catch (Exception ex) {
            Log.e("findAppropriateBroker", "Exception", ex);
            throw new RuntimeException(ex);
        }
    }

    private AbstractMap.SimpleEntry<String, Integer> getMasterBrokerIpPort() {
        NodeDetails masterBrokerDetails = this.getMasterBrokerDetails();

        if (masterBrokerDetails != null) {
            String masterBrokerIp = masterBrokerDetails.getIpAddress();
            int masterBrokerPort = masterBrokerDetails.getPort();

//            return new AbstractMap.SimpleEntry<>("10.0.2.2", 8080);
            return new AbstractMap.SimpleEntry<>(masterBrokerIp, masterBrokerPort);
        }

        String masterBrokerIp = applicationSettings.get("master_ip");
        int masterBrokerPort = Integer.parseInt(applicationSettings.get("master_port"));
        return new AbstractMap.SimpleEntry<>(masterBrokerIp, masterBrokerPort);
    }

    private List<MusicFile> getTrackResults() throws Exception {
        List<MusicFile> musicFiles = this.retrieveMusicFiles();

        if (musicFiles == null || musicFiles.isEmpty()) {
            // TODO - Add Exception Message
//            throw new Exception(PropertiesHelper.getProperty("consumer.retrieve.tracks.list.empty"))
            throw new Exception();
        }

        return musicFiles;
    }

    private List<MusicFile> retrieveMusicFiles() throws Exception {
        List<MusicFile> musicFiles = null;
        try {
            Object response = NetworkHelper.doObjectRequest(this.connection, this.artistName);

            if (response instanceof List) musicFiles = (List<MusicFile>) response;
            else if (response instanceof PublisherNotFoundException)
                throw (PublisherNotFoundException) response;
        } catch (IOException | ClassNotFoundException ex) {
            // TODO - Add Exception Message
//            throw new Exception(String.format(
//                    PropertiesHelper.getProperty("consumer.retrieve.tracks.list.failed"),
//                    this.connectedBroker.getNodeDetails().getName())
//            );
        }

        return musicFiles;
    }

    private boolean isAppropriateBrokerMaster() {
        return this.connectedBrokerDetails.equals(getMasterBrokerDetails());
    }
}