package gr.aueb.ds.music.framework.requests;

import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.enums.NodeType;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Publisher;

import java.io.Serializable;

public class NodeRequest implements Serializable {

    private NodeType nodeType;
    private NodeDetails nodeDetails;

    // For Broker Only
    private Broker broker;
    // For Publisher Only
    private Publisher publisher;

    public NodeRequest() { }

    public NodeRequest(NodeType nodeType, NodeDetails nodeDetails) {
        this.nodeType = nodeType;
        this.nodeDetails = nodeDetails;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public NodeDetails getNodeDetails() {
        return nodeDetails;
    }

    public void setNodeDetails(NodeDetails nodeDetails) {
        this.nodeDetails = nodeDetails;
    }

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
