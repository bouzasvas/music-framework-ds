package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.commons.SystemExitCodes;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.commons.ConsoleColors;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Node;

import java.io.IOException;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.Optional;
import java.util.Scanner;

public class ConsumerImplementation extends NodeAbstractImplementation implements Consumer {

    protected Socket socket;
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

    }

    @Override
    public void disconnect(Broker broker, ArtistName artistName) {

    }

    @Override
    public void playData(ArtistName artistName, Value value) {
//        Broker appropriateBrokerForArtist = this.
    }

    @Override
    public void init() {
        Scanner scanner = new Scanner(System.in);

        String artistName = "";
        // Init Consumer & Ask continuously for Songs
        while (!artistName.equals("-1")) {
            LogHelper.userInputWithColor(ConsoleColors.BLUE_BOLD, "Type the Artist Name you want to listen: ");

            artistName = scanner.nextLine();
            this.artistName = new ArtistName(artistName);

            this.connect();
        }
    }

    @Override
    public void connect() {
        this.connectToAppropriateBroker();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void updateNodes() {

    }

    private void connectToAppropriateBroker() {
        try {
            AbstractMap.SimpleEntry<String, Integer> masterBrokerIpPort = this.getMasterBrokerIpPort();
            this.socket = NetworkHelper.initConnection(masterBrokerIpPort.getKey(), masterBrokerIpPort.getValue());

        }
        catch (Exception ex) {
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
