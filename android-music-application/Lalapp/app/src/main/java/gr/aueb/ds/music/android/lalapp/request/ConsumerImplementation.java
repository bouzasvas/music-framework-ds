package gr.aueb.ds.music.android.lalapp.request;

//import gr.aueb.ds.music.framework.error.FileChunksProcessingException;

import android.util.Log;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;

import gr.aueb.ds.music.framework.error.PublisherNotFoundException;
import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.model.network.Connection;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

//import gr.aueb.ds.music.framework.helper.FileSystemHelper;

public class ConsumerImplementation extends NodeAbstractImplementation implements Consumer {

    protected transient Connection connection;
    protected NodeDetails connectedBrokerDetails;
    public ArtistName artistName;

    public List<MusicFile> musicFiles;

    public ConsumerImplementation() {
        super();
        this.nodeDetails = new NodeDetails();
    }

    public ConsumerImplementation(String name) {
        this();
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
//        if (FileSystemHelper.fileHasDownloaded(value.getMusicFile())) return;
//
//        try {
//            saveChunkInFileSystem(value);
//        } catch (FileChunksProcessingException ex) {
//            // TODO -- Error Handling & Logging
////            LogHelper.errorWithParams(this, ex.getMessage(), value.getMusicFile().toString());
//        }
    }

    private void downloadSelectedTrack(Value value) {
        MusicFile finalMusicFile = null;
//        try {
//            List<byte[]> fileChunks = this.retrieveChunksOfMusicFile(value);
//            finalMusicFile = this.mergeChunks(value, fileChunks);
//
//            FileSystemHelper.saveMusicFileToFileSystem(finalMusicFile, true);
//        } catch (FileChunksProcessingException ex) {
//            // TODO -- Error Handling & Logging
////            LogHelper.errorWithParams(this, ex.getMessage(), value.getMusicFile().toString());
//        } catch (IOException ex) {
//            // TODO -- Error Handling & Logging
////            LogHelper.errorWithParams(this, PropertiesHelper.getProperty("consumer.save.file.to.disk"), value.getMusicFile().toString());
//        }
    }

    @Override
    public void init() {
        // Make the Connection
        this.connect();

        // Register to Broker
        this.register(this.connectedBrokerDetails, this.artistName);

        // Get Tracks from Broker and Print Results
        List<MusicFile> musicFiles;
        try {
            this.musicFiles = this.getTracksFromBroker();
        } catch (Exception ex) {
            // TODO -- Error Handling & Logging
//            LogHelper.error(this, ex.getMessage());
        }

        // Prompt user to choose Track (based on number)
//        int trackNo = promptUserForTrackNo(scanner, musicFiles);
        // Selected Track from List
//        Value value = new Value(musicFiles.get(trackNo - 1));

        // Search if File already Exists
//        if (FileSystemHelper.fileHasDownloaded(value.getMusicFile())) continue;

        // Choose whether to Download or Play file
//        boolean download = promptUserToChooseDownloadOrPlay(scanner);
//        if (download) downloadSelectedTrack(value);

        // Do the Request for specific Track to Broker
//        this.playData(this.artistName, value);
    }

    @Override
    public void connect() {
        this.findAppropriateBroker();
    }

    @Override
    public void disconnect() {

    }


    private List<MusicFile> getTracksFromBroker() throws Exception {
        // Get Track Results
        List<MusicFile> musicFiles = this.getTrackResults();

        // TODO -- Pass tracks to Adapter

        return musicFiles;
    }

//    private void saveChunkInFileSystem(Value value) throws FileChunksProcessingException {
//        int chunkNo = 1;
//
//        try {
//            // Do Request and Retrieve Chunks one by one
//            MusicFile musicFile = NetworkHelper.doObjectRequest(this.connection, value);
//            musicFile.setTrackName(musicFile.getTrackName().concat("_chunk" + chunkNo++));
//            FileSystemHelper.saveMusicFileToFileSystem(musicFile, false);
//            while ((musicFile = (MusicFile) this.connection.getIs().readObject()) != null) {
//                musicFile.setTrackName(musicFile.getTrackName().concat("_chunk" + chunkNo++));
//                FileSystemHelper.saveMusicFileToFileSystem(musicFile, false);
//            }
//        } catch (IOException | ClassNotFoundException ex) {
//            throw new FileChunksProcessingException("consumer.get.file.chunks");
//        }
//    }

//    private List<byte[]> retrieveChunksOfMusicFile(Value value) throws FileChunksProcessingException {
//        List<byte[]> musicFileBytesList = new ArrayList<>();
//
//        try {
//            // Do Request and Retrieve Chunks one by one
//            MusicFile musicFile = NetworkHelper.doObjectRequest(this.connection, value);
//            musicFileBytesList.add(musicFile.getMusicFileExtract());
//
//            while ((musicFile = (MusicFile) this.connection.getIs().readObject()) != null) {
//                musicFileBytesList.add(musicFile.getMusicFileExtract());
//            }
//        } catch (IOException | ClassNotFoundException ex) {
//            throw new FileChunksProcessingException("consumer.get.file.chunks");
//        }
//
//        return musicFileBytesList;
//    }
//
//    private MusicFile mergeChunks(Value value, List<byte[]> musicFileChunks) throws FileChunksProcessingException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        MusicFile wholeFile = new MusicFile(value.getMusicFile());
//        try {
//            for (byte[] bytes : musicFileChunks) {
//                byteArrayOutputStream.write(bytes);
//            }
//
//            wholeFile.setMusicFileExtract(byteArrayOutputStream.toByteArray());
//        } catch (IOException e) {
//            throw new FileChunksProcessingException("consumer.merge.file.chunks");
//        }
//
//        return wholeFile;
//    }

    private void findAppropriateBroker() {
        try {
            AbstractMap.SimpleEntry<String, Integer> masterBrokerIpPort = this.getMasterBrokerIpPort();
            this.connection = new Connection(NetworkHelper.initConnection(masterBrokerIpPort.getKey(), masterBrokerIpPort.getValue()));

            // Get appropriate broker
            this.connectedBrokerDetails = NetworkHelper.doObjectRequest(this.connection, this.artistName);
        } catch (Exception ex) {
            // TODO - Logging & Error Handling
            Log.e("findAppropriateBroker", "Exception", ex);
//            LogHelper.error(this,
//                    String.format(PropertiesHelper.getProperty("consumer.node.broker.connection.failed"),
//                            this.nodeDetails.getName()));
//            System.exit(SystemExitCodes.MASTER_NOT_FOUND_ERROR.getCode());
        }
    }

    private AbstractMap.SimpleEntry<String, Integer> getMasterBrokerIpPort() {
        NodeDetails masterBrokerDetails = this.getMasterBrokerDetails();

        if (masterBrokerDetails != null) {
            String masterBrokerIp = masterBrokerDetails.getIpAddress();
            int masterBrokerPort = masterBrokerDetails.getPort();

            return new AbstractMap.SimpleEntry<>("10.0.2.2", 8080);
//            return new AbstractMap.SimpleEntry<>(masterBrokerIp, masterBrokerPort);
        }

        // TODO - Shared Preferences
        return new AbstractMap.SimpleEntry<>("10.0.2.2", 8080);
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}