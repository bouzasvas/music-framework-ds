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

        if (!(publisher.isPublisherDown() || registeredPublishers.contains(publisher))) {
            registeredPublishers.add(publisher);
        }

        if (publisher.isPublisherDown()) {
            registeredPublishers.remove(publisher);
            reOrganizeArtists(publisher);
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

                            return artistStart.toLowerCase().compareTo(artistName.toLowerCase().substring(0, 1)) <= 0
                                    && artistEnd.toLowerCase().compareTo(artistName.toLowerCase().substring(0, 1)) >= 0;
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

    @Override
    public void updateNodes() {
        super.updateNodes();

        List<Publisher> publishersToBeRemoved = new ArrayList<>();
        // Check Connectivity with Publishers
        for (Publisher publisher : registeredPublishers) {
            String publisherIp  = publisher.getNodeDetails().getIpAddress();
            int publisherPort   = publisher.getNodeDetails().getPort();

            try {
                NetworkHelper.checkIfHostIsAlive(publisherIp, publisherPort);
            } catch (IOException e) {
                LogHelper.errorWithParams(this, PropertiesHelper.getProperty("broker.publisher.disconnected"), publisher.getNodeDetails().getName());
                publishersToBeRemoved.add(publisher);
            }
        }
        publishersToBeRemoved.forEach(Publisher::disconnect);

        // TODO - Check Connectivity with Consumers
        // Consumer are not registered in any list - Do we need them?
        for (Consumer consumer : registeredUsers) {

        }
    }

    @Override
    public Connection getPublisherConnection() {
        return this.publisherConnection;
    }

    @Override
    public BrokerIndicator getBrokerIndicator() {
        return brokerIndicator;
    }


    /**
     * This method re-organizes Artists that Publisher are responsible for when a Publisher disconnects from Network.
     *
     * <b>Note: This method works only if 2 Publishers are connected to Network and 1 disconnects</b>
     *
     * @param removedPublisher {@link gr.aueb.ds.music.framework.nodes.api.Publisher} The publisher that disconnected from Network
     */
    private void reOrganizeArtists(Publisher removedPublisher) {
        String[] artistRange = removedPublisher.getNodeDetails().getArtistRange();
        String fromArtistRemoved = artistRange[0];
        String toArtistRemoved = artistRange[1];

        this.registeredPublishers.forEach(publisher -> {
            String[] pubArtistRange = publisher.getNodeDetails().getArtistRange();
            String fromArtist = pubArtistRange[0];
            String toArtist = pubArtistRange[1];

            if (fromArtistRemoved.compareTo(fromArtist) < 1) {
                pubArtistRange[0] = fromArtistRemoved;
            }

            if (toArtistRemoved.compareTo(toArtist) > 1) {
                pubArtistRange[1] = toArtistRemoved;
            }
        });
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
