package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Publisher;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class PublisherImplementation implements Publisher {

    protected NodeDetails nodeDetails;
    protected Socket socket;

    public PublisherImplementation() throws IOException {
        this.nodeDetails = new NodeDetails();
    }

    public PublisherImplementation(String name, int port) throws IOException {
        this();
        this.nodeDetails.setName(name);
        this.nodeDetails.setIpAddress(NetworkHelper.getCurrentIpAddress());
        this.nodeDetails.setPort(port);

        this.init();
    }

    @Override
    public List<Broker> getBrokers() {
        return null;
    }

    @Override
    public void init() {

    }

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
    public Broker hashTopic(ArtistName artistName) {
        return null;
    }

    @Override
    public void push(ArtistName artistName, Value value) {

    }

    @Override
    public void notifyFailure(Broker broker) {

    }

    public NodeDetails getNodeDetails() {
        return nodeDetails;
    }

    public void setNodeDetails(NodeDetails nodeDetails) {
        this.nodeDetails = nodeDetails;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
