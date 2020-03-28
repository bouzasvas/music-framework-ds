package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.error.PublisherNotFoundException;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.network.Connection;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.parallel.actions.ActionsForClients;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BrokerImplementation extends NodeAbstractImplementation implements Broker {

    List<Consumer> registeredUsers = new ArrayList<>();
    List<Publisher> registeredPublishers = new ArrayList<>();

    // Connection to Publisher
    private transient Connection publisherConnection;

    public enum BrokerIndicator {
        TO_ADD,
        TO_DELETE;
    }

    private BrokerIndicator brokerIndicator;

    public BrokerImplementation() {
        super(true);
        this.nodeDetails = new NodeDetails();
    }

    public BrokerImplementation(String name, int port) throws IOException {
        this();
        this.nodeDetails.setName(name);
        this.nodeDetails.setIpAddress(NetworkHelper.getCurrentIpAddress());
        this.nodeDetails.setPort(port);
    }

    @Override
    public void init() throws IOException {
        // Connect Broker to Network
        this.connect();

        // Init Server
        ServerSocket serverSocket = NetworkHelper.initServer(this.getNodeDetails().getPort());
        LogHelper.info(this, PropertiesHelper.getProperty("broker.server.init"));

        // Continuous Listening for Requests
        while (true) {
            Socket connectionSocket = serverSocket.accept();

            // Start a Thread for Each Client Connection
            Thread connectionThread = new Thread(new ActionsForClients(this, connectionSocket));
            connectionThread.start();
        }
    }

    @Override
    public void calculateKeys() {

    }

    @Override
    public Publisher acceptConnection(Publisher publisher) {
        List<Publisher> registeredPublishers = this.getRegisteredPublishers();

        if (!registeredPublishers.contains(publisher)) {
            registeredPublishers.add(publisher);
        }

        return publisher;
    }

    @Override
    public Consumer acceptConnection(Consumer consumer) {
        List<Consumer> registeredUsers = this.getRegisteredUsers();

        if (!registeredUsers.contains(consumer)) {
            registeredUsers.add(consumer);
        }

        return consumer;
    }

    @Override
    public void notifyPublisher(String artistName) throws PublisherNotFoundException {
        Publisher publisher =
                this.registeredPublishers
                        .stream()
                        .filter(p -> {
                            String[] artistRange = p.getNodeDetails().getArtistRange();
                            String artistStart = artistRange[0];
                            String artistEnd = artistRange[1];

                            return artistStart.compareTo(artistName.substring(0, 1)) <= 0
                                    && artistEnd.compareTo(artistName.substring(0, 1)) >= 0;
                        })
                        .findFirst()
                        .orElseThrow(PublisherNotFoundException::new);

        try {
            Socket publisherSocket = new Socket(publisher.getNodeDetails().getIpAddress(), publisher.getNodeDetails().getPort());
            this.publisherConnection = new Connection(publisherSocket);
        } catch (IOException e) {
            LogHelper.errorWithParams(this, PropertiesHelper.getProperty("broker.publisher.connection.failed"), publisher.getNodeDetails().getName());
        }

    }

    @Override
    public List<MusicFile> pull(ArtistName artistName) throws PublisherNotFoundException {
        this.notifyPublisher(artistName.getArtistName());

        List<MusicFile> musicFiles = null;
        try {
            musicFiles = NetworkHelper.doObjectRequest(this.publisherConnection, artistName);
        } catch (IOException | ClassNotFoundException e) {
            LogHelper.errorWithParams(this, PropertiesHelper.getProperty("broker.publisher.request.failed"), artistName.getArtistName());
        }

        return musicFiles;
    }

    // Node Methods
    @Override
    public void connect() {
        this.brokerIndicator = BrokerIndicator.TO_ADD;
        getBrokers().add(this);

        this.updateNodes();
        LogHelper.info(this, PropertiesHelper.getProperty("broker.connect.message"));
    }

    @Override
    public void disconnect() {
        this.brokerIndicator = BrokerIndicator.TO_DELETE;
        getBrokers().remove(this);

        LogHelper.info(this, PropertiesHelper.getProperty("broker.disconnect"));

        // Better not doing this because of endless loop
        // this.updateNodes();
    }

    public BrokerIndicator getBrokerIndicator() {
        return brokerIndicator;
    }

    public void setBrokerIndicator(BrokerIndicator brokerIndicator) {
        this.brokerIndicator = brokerIndicator;
    }

    public List<Consumer> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<Consumer> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public List<Publisher> getRegisteredPublishers() {
        return registeredPublishers;
    }

    public void setRegisteredPublishers(List<Publisher> registeredPublishers) {
        this.registeredPublishers = registeredPublishers;
    }
}
