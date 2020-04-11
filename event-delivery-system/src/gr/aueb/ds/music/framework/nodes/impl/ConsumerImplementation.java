package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.commons.SystemExitCodes;
import gr.aueb.ds.music.framework.error.FileChunksProcessingException;
import gr.aueb.ds.music.framework.error.PublisherNotFoundException;
import gr.aueb.ds.music.framework.helper.FileSystemHelper;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.commons.ConsoleColors;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.model.network.Connection;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Node;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.IntStream;

public class ConsumerImplementation extends NodeAbstractImplementation implements Consumer {

    protected transient Connection connection;
    protected Broker connectedBroker;
    protected ArtistName artistName;

    public ConsumerImplementation() {
        super(true);
        this.nodeDetails = new NodeDetails();
    }

    public ConsumerImplementation(String name) {
        this();
        this.nodeDetails.setName(name);
    }

    @Override
    public void register(Broker broker, ArtistName artistName) {
        try {
            // If Appropriate Broker is Master continue the communication
            if (!isAppropriateBrokerMaster()) {
                this.connection.close();
                this.connection = new Connection(NetworkHelper.initConnection(broker.getNodeDetails().getIpAddress(), broker.getNodeDetails().getPort()));
            }

            // The following requests is not for Broker Discovery
            this.artistName = new ArtistName(this.artistName.getArtistName(), false);
        } catch (IOException ex) {
            LogHelper.error(this, String.format(PropertiesHelper.getProperty("consumer.register.broker.error"), broker.getNodeDetails().getName()));
        }
    }

    @Override
    public void disconnect(Broker broker, ArtistName artistName) {

    }

    @Override
    public void playData(ArtistName artistName, Value value) {
        if (FileSystemHelper.fileHasDownloaded(value.getMusicFile())) return;

        try {
            saveChunkInFileSystem(value);
        } catch (FileChunksProcessingException ex) {
            LogHelper.errorWithParams(this, ex.getMessage(), value.getMusicFile().toString());
        }
    }

    private void downloadSelectedTrack(Value value) {
        MusicFile finalMusicFile = null;
        try {
            List<byte[]> fileChunks = this.retrieveChunksOfMusicFile(value);
            finalMusicFile = this.mergeChunks(value, fileChunks);

            FileSystemHelper.saveMusicFileToFileSystem(finalMusicFile, true);
        } catch (FileChunksProcessingException ex) {
            LogHelper.errorWithParams(this, ex.getMessage(), value.getMusicFile().toString());
        } catch (IOException ex) {
            LogHelper.errorWithParams(this, PropertiesHelper.getProperty("consumer.save.file.to.disk"), value.getMusicFile().toString());
        }
    }

    @Override
    public void init() {
        Scanner scanner = new Scanner(System.in);

        String artistName = "";
        // Init Consumer & Ask continuously for Songs
        while (!artistName.equals("-1")) {
            clearConsole();

            LogHelper.userInputWithColor(ConsoleColors.BLUE_BOLD, PropertiesHelper.getProperty("consumer.menu.choose.artist"));

            artistName = scanner.nextLine();
            if (artistName.equals("-1")) break;
            this.artistName = new ArtistName(artistName, true);

            // Make the Connection
            this.connect();

            // Register to Broker
            this.register(this.connectedBroker, this.artistName);

            // Get Tracks from Broker and Print Results
            List<MusicFile> musicFiles;
            try {
                musicFiles = this.getTracksFromBroker();
            }
            catch (Exception ex) {
                LogHelper.error(this, ex.getMessage());
                continue;
            }

            // Prompt user to choose Track (based on number)
            int trackNo = promptUserForTrackNo(scanner, musicFiles);
            // Selected Track from List
            Value value = new Value(musicFiles.get(trackNo - 1));

            // Search if File already Exists
            if (FileSystemHelper.fileHasDownloaded(value.getMusicFile())) continue;

            // Choose whether to Download or Play file
            boolean download = promptUserToChooseDownloadOrPlay(scanner);
            if (download) downloadSelectedTrack(value);

            // Do the Request for specific Track to Broker
            this.playData(this.artistName, value);
        }
    }

    @Override
    public void connect() {
        this.findAppropriateBroker();
    }

    @Override
    public void disconnect() {

    }

    private void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private Integer promptUserForTrackNo(Scanner scanner, List<MusicFile> musicFiles) {
        int minimumAllowedNumber = 1;
        int maximumAllowedNumber = musicFiles.size();

        Integer trackNo = null;
        while (trackNo == null || (trackNo < minimumAllowedNumber) || trackNo > maximumAllowedNumber) {
            LogHelper.userInputWithColor(ConsoleColors.BLUE_BOLD, PropertiesHelper.getProperty("consumer.menu.choose.track"));

            try {
                trackNo = Integer.parseInt(scanner.nextLine());

                // Explicitly throw Exception when Number out of bounds
                if (trackNo < minimumAllowedNumber || trackNo > maximumAllowedNumber) {
                    throw new NumberFormatException();
                }
            }
            catch (NumberFormatException nfe) {
                LogHelper.info(this,
                        String.format(PropertiesHelper.getProperty("consumer.node.choose.track.number.error"), minimumAllowedNumber, maximumAllowedNumber));
            }
        }

        return trackNo;
    }

    private boolean promptUserToChooseDownloadOrPlay(Scanner scanner) {
        String choice = "";
        while (!(choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("n"))) {
            LogHelper.userInputWithColor(ConsoleColors.BLUE_BOLD, PropertiesHelper.getProperty("consumer.menu.download.or.play"));
            choice = scanner.nextLine();

            if (!(choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("n"))) {
                LogHelper.error(this, PropertiesHelper.getProperty("consumer.menu.download.or.play.failed.input"));
                System.out.println();
            }
        }

        return choice.equalsIgnoreCase("Y");
    }

    private List<MusicFile> getTracksFromBroker() throws Exception {
        // Get Track Results
        List<MusicFile> musicFiles = this.getTrackResults();

        LogHelper.userInputWithColor(ConsoleColors.YELLOW_BOLD,
                String.format(
                        PropertiesHelper.getProperty("consumer.retrieve.tracks.list.title"),
                        this.artistName.getArtistName()),
                true);

        IntStream.range(0, musicFiles.size())
                .forEach(index -> {
                    MusicFile musicFile = musicFiles.get(index);

                    LogHelper.userInputWithColor(ConsoleColors.YELLOW_BOLD, "\t" + (index+1) + ". " + musicFile.toString(), true);
                });

        return musicFiles;
    }

    private void saveChunkInFileSystem(Value value) throws FileChunksProcessingException {
        int chunkNo = 1;

        try {
            // Do Request and Retrieve Chunks one by one
            MusicFile musicFile = NetworkHelper.doObjectRequest(this.connection, value);
            musicFile.setTrackName(musicFile.getTrackName().concat("_chunk" + chunkNo++));
            FileSystemHelper.saveMusicFileToFileSystem(musicFile, false);
            while ((musicFile = (MusicFile) this.connection.getIs().readObject()) != null) {
                musicFile.setTrackName(musicFile.getTrackName().concat("_chunk" + chunkNo++));
                FileSystemHelper.saveMusicFileToFileSystem(musicFile, false);
            }
        }
        catch (IOException | ClassNotFoundException ex) {
            throw new FileChunksProcessingException("consumer.get.file.chunks");
        }
    }

    private List<byte[]> retrieveChunksOfMusicFile(Value value) throws FileChunksProcessingException {
        List<byte[]> musicFileBytesList = new ArrayList<>();

        try {
            // Do Request and Retrieve Chunks one by one
            MusicFile musicFile = NetworkHelper.doObjectRequest(this.connection, value);
            musicFileBytesList.add(musicFile.getMusicFileExtract());

            while ((musicFile = (MusicFile) this.connection.getIs().readObject()) != null) {
                musicFileBytesList.add(musicFile.getMusicFileExtract());
            }
        }
        catch (IOException | ClassNotFoundException ex) {
            throw new FileChunksProcessingException("consumer.get.file.chunks");
        }

        return musicFileBytesList;
    }

    private MusicFile mergeChunks(Value value, List<byte[]> musicFileChunks) throws FileChunksProcessingException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        MusicFile wholeFile = new MusicFile(value.getMusicFile());
        try {
            for (byte[] bytes : musicFileChunks) {
                byteArrayOutputStream.write(bytes);
            }

            wholeFile.setMusicFileExtract(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new FileChunksProcessingException("consumer.merge.file.chunks");
        }

        return wholeFile;
    }

    private void findAppropriateBroker() {
        try {
            AbstractMap.SimpleEntry<String, Integer> masterBrokerIpPort = this.getMasterBrokerIpPort();
            this.connection = new Connection(NetworkHelper.initConnection(masterBrokerIpPort.getKey(), masterBrokerIpPort.getValue()));

            // Get appropriate broker
            this.connectedBroker = NetworkHelper.doObjectRequest(this.connection, this.artistName);
        } catch (Exception ex) {
            LogHelper.error(this,
                    String.format(PropertiesHelper.getProperty("consumer.node.broker.connection.failed"),
                            this.nodeDetails.getName()));
            System.exit(SystemExitCodes.MASTER_NOT_FOUND_ERROR.getCode());
        }
    }

    private AbstractMap.SimpleEntry<String, Integer> getMasterBrokerIpPort() {
        Broker masterBroker = this.getMasterBroker();

        String masterBrokerIp = Optional.ofNullable(masterBroker)
                .map(Node::getNodeDetails)
                .map(NodeDetails::getIpAddress)
                .orElse(PropertiesHelper.getProperty("master.broker.ip"));

        int masterBrokerPort = Optional.ofNullable(masterBroker)
                .map(Node::getNodeDetails)
                .map(NodeDetails::getPort)
                .orElse(Integer.parseInt(PropertiesHelper.getProperty("master.broker.port")));

        return new AbstractMap.SimpleEntry<>(masterBrokerIp, masterBrokerPort);
    }

    private List<MusicFile> getTrackResults() throws Exception {
        List<MusicFile> musicFiles = this.retrieveMusicFiles();

        return Optional
                .ofNullable(musicFiles)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new Exception(PropertiesHelper.getProperty("consumer.retrieve.tracks.list.empty")));
    }

    private List<MusicFile> retrieveMusicFiles() throws Exception {
        List<MusicFile> musicFiles = null;
        try {
            Object response = NetworkHelper.doObjectRequest(this.connection, this.artistName);

            if (response instanceof List) musicFiles = (List<MusicFile>) response;
            else if (response instanceof PublisherNotFoundException) throw (PublisherNotFoundException) response;
        } catch (IOException | ClassNotFoundException ex) {
            throw new Exception(String.format(
                    PropertiesHelper.getProperty("consumer.retrieve.tracks.list.failed"),
                    this.connectedBroker.getNodeDetails().getName())
            );
        }

        return musicFiles;
    }

    private boolean isAppropriateBrokerMaster() {
        return this.connectedBroker.equals(getMasterBroker());
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
