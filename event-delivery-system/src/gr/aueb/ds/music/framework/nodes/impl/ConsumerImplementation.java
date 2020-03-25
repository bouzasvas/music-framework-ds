package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.commons.SystemExitCodes;
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

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.stream.IntStream;

public class ConsumerImplementation extends NodeAbstractImplementation implements Consumer {

    protected transient Connection connection;
    protected Broker connectedBroker;
    protected ArtistName artistName;

    public ConsumerImplementation() {
        this.nodeDetails = new NodeDetails();
    }

    public ConsumerImplementation(String name) throws IOException {
        this();
        this.nodeDetails.setName(name);

        this.init();
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
        // TODO -- Implement
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

            // Do the Request for specific Track to Broker
            Value value = new Value(musicFiles.get(trackNo - 1));
            this.playData(this.artistName, value);
        }
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

    @Override
    public void connect() {
        this.findAppropriateBroker();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void updateNodes() {

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
        List<MusicFile> musicFiles;
        try {
            musicFiles = NetworkHelper.doObjectRequest(this.connection, this.artistName);
        } catch (Exception ex) {

            throw new Exception(String.format(
                    PropertiesHelper.getProperty("consumer.retrieve.tracks.list.failed"),
                    this.connectedBroker.getNodeDetails().getName())
            );
        }

        return Optional
                .ofNullable(musicFiles)
                .orElseThrow(() -> new Exception(PropertiesHelper.getProperty("consumer.retrieve.tracks.list.empty")));
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
