package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ConsumerImplementation implements Consumer {

    protected NodeDetails nodeDetails;
    protected Socket socket;

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

    }

    @Override
    public void init() throws IOException {
    }

    @Override
    public List<Broker> getBrokers() {
        return null;
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
