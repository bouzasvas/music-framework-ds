package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.parallel.actions.ActionsForClients;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PublisherImplementation extends NodeAbstractImplementation implements Publisher {

    protected ServerSocket serverSocket;
    protected Socket socket;

    public PublisherImplementation() throws IOException {
        super(true);
        this.nodeDetails = new NodeDetails();
    }

    public PublisherImplementation(String name, int port) throws IOException {
        this();
        this.nodeDetails.setName(name);
        this.nodeDetails.setIpAddress(NetworkHelper.getCurrentIpAddress());
        this.nodeDetails.setPort(port);
    }

    public PublisherImplementation(String name, int port, String... artistRange) throws IOException {
        this(name, port);
        this.nodeDetails.setArtistRange(artistRange);
    }

    @Override
    public void init() throws IOException {
        this.connect();

        // Init Server for Brokers-Publisher communication
        this.initServer();
    }

    @Override
    public void connect() {
        // Connect with Master Broker and get brokers list
        this.getBrokers();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void updateNodes() {

    }

    @Override
    public Broker hashTopic(ArtistName artistName) {
        return null;
    }

    @Override
    public void push(ArtistName artistName, Value value) {

    }

    @Override
    public void notifyFailure(Broker broker) {

    }

    private void initServer() throws IOException {
        this.serverSocket = new ServerSocket(this.getNodeDetails().getPort());

        LogHelper.info(this, PropertiesHelper.getProperty("publisher.server.init"));

        while (true) {
            new Thread(new ActionsForClients(this, this.serverSocket.accept())).start();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
