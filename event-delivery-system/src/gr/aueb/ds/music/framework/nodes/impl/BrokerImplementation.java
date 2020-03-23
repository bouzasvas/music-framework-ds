package gr.aueb.ds.music.framework.nodes.impl;

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
import java.util.List;

public class BrokerImplementation extends NodeAbstractImplementation implements Broker {

    public enum BrokerIndicator {
        TO_ADD,
        TO_DELETE;
    }

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

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void updateNodes() {

    }

    @Override
    public List<Broker> getBrokers() {
        return null;
    }

    public BrokerIndicator getBrokerIndicator() {
        return brokerIndicator;
    }

    public void setBrokerIndicator(BrokerIndicator brokerIndicator) {
        this.brokerIndicator = brokerIndicator;
    }
}
