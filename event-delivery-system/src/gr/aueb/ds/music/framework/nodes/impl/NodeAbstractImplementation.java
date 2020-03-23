package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class NodeAbstractImplementation implements Node, Serializable {
    private static final long serialVersionUID = 5792977548048762220L;

    protected List<Broker> brokers = new ArrayList<>();
    protected NodeDetails nodeDetails;

    // Getters & Setters
    public List<Broker> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<Broker> brokers) {
        this.brokers = brokers;
    }

    public NodeDetails getNodeDetails() {
        return nodeDetails;
    }

    public void setNodeDetails(NodeDetails nodeDetails) {
        this.nodeDetails = nodeDetails;
    }


}
