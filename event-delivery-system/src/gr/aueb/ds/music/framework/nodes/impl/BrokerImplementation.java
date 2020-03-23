package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.parallel.actions.ActionsForClients;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BrokerImplementation extends NodeAbstractImplementation implements Broker {

    List<Consumer> registeredUsers = new ArrayList<>();
    List<Publisher> registeredPublishers = new ArrayList<>();

    public enum BrokerIndicator {
        TO_ADD,
        TO_DELETE;
    }

    private transient ServerSocket serverSocket;
    private BrokerIndicator brokerIndicator;

    public BrokerImplementation() {
        this.nodeDetails = new NodeDetails();
    }

    public BrokerImplementation(String name, int port) throws IOException {
        this();
        this.nodeDetails.setName(name);
        this.nodeDetails.setIpAddress(NetworkHelper.getCurrentIpAddress());
        this.nodeDetails.setPort(port);

        this.init();
    }

    @Override
    public void init() throws IOException {
        // Connect Broker to Network
        this.connect();

        // Init Server
        this.serverSocket = NetworkHelper.initServer(this.getNodeDetails().getPort());

        LogHelper.info(this, PropertiesHelper.getProperty("broker.server.init"));

        // Continuous Listening for Requests
        while (true) {
            // Start a Thread for Each Client Connection
            new Thread(new ActionsForClients(this, this.serverSocket.accept())).start();
        }
    }

    @Override
    public void calculateKeys() {

    }

    @Override
    public Publisher acceptConnection(Publisher publisher) {
        return null;
    }

    @Override
    public Consumer acceptConnection(Consumer consumer) {
        return null;
    }

    @Override
    public void notifyPublisher(String artistName) {

    }

    @Override
    public void pull(ArtistName artistName) {

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

        // TODO - Check this
        // this.updateNodes();
    }

    @Override
    public void updateNodes() {
        List<Broker> connectedBrokers = getBrokers();

        // Check Connectivity with Brokers
        for (Broker broker : connectedBrokers) {
            BrokerImplementation brokerImpl = (BrokerImplementation) broker;
            if (!brokerImpl.equals(this)) {
                try {
                    String ip = brokerImpl.getNodeDetails().getIpAddress();
                    int port = brokerImpl.getNodeDetails().getPort();
                    NetworkHelper.checkIfHostIsAlive(ip, port);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogHelper.error(this, String.format(PropertiesHelper.getProperty("broker.liveness.failed"), brokerImpl.getNodeDetails().getName()));
                    brokerImpl.disconnect();
                }
            }
        }
    }

    @Override
    public List<Broker> getBrokers() {
        // If Broker is not Master Broker then Retrieve Brokers from Master
        if (!this.isMasterBroker()) {
            Broker masterBroker = this.getMasterBroker();
            this.setBrokers(masterBroker.getBrokers());
        }

        return this.brokers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return  true;
        if (obj instanceof BrokerImplementation) {
            BrokerImplementation other = (BrokerImplementation) obj;

            NodeDetails thisNode = this.getNodeDetails();
            NodeDetails otherNode = other.getNodeDetails();

            return  thisNode.getName().equals(otherNode.getName()) &&
                    thisNode.getIpAddress().equals(otherNode.getIpAddress()) &&
                    thisNode.getPort() == otherNode.getPort();
        }
        else {
            return false;
        }
    }

    // Helper Methods
    private boolean isMasterBroker() {
        return this.getNodeDetails().getPort() == Integer.parseInt(PropertiesHelper.getProperty("master.broker.port"));
    }

    private Broker getMasterBroker() {
        Broker masterBroker = null;

        // Suppose Master Broker listens to localhost:8080
        try (Socket socket =
                     NetworkHelper.initConnection(
                             InetAddress.getLoopbackAddress().getHostAddress(),
                             Integer.parseInt(PropertiesHelper.getProperty("master.broker.port")))
        ) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(this);
            masterBroker = (Broker) objectInputStream.readObject();
        } catch (Exception ex) {
            LogHelper.error(this, String.format(PropertiesHelper.getProperty("broker.master.node.required"), PropertiesHelper.getProperty("master.broker.port")));
            System.exit(-100);
        }

        System.out.println("getMasterBroker() :: Method Returned Master Broker");
        return masterBroker;
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
