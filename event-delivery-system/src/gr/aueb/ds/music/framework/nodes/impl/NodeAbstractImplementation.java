package gr.aueb.ds.music.framework.nodes.impl;

import gr.aueb.ds.music.framework.commons.SystemExitCodes;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Node;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class NodeAbstractImplementation implements Node, Serializable {
    private static final long serialVersionUID = 5792977548048762220L;

    protected List<Broker> brokers = new ArrayList<>();
    protected NodeDetails nodeDetails;

    // Getters & Setters

    public void setBrokers(List<Broker> brokers) {
        this.brokers = brokers;
    }

    public NodeDetails getNodeDetails() {
        return nodeDetails;
    }

    public void setNodeDetails(NodeDetails nodeDetails) {
        this.nodeDetails = nodeDetails;
    }

    // Helper Methods
    @Override
    public List<Broker> getBrokers() {
        // If Broker is not Master Broker then Retrieve Brokers from Master
        if (!this.isMasterBroker()) {
            Broker masterBroker = this.getMasterBroker();
            this.setBrokers(masterBroker.getBrokers());
        }

        return this.brokers;
    }

    public boolean isMasterBroker() {
        return this.getNodeDetails().getPort() == Integer.parseInt(PropertiesHelper.getProperty("master.broker.port"));
    }

    protected Broker getMasterBroker() {
        Broker masterBroker = null;

        String masterBrokerIp = PropertiesHelper.getProperty("master.broker.ip");
        int masterBrokerPort = Integer.parseInt(PropertiesHelper.getProperty("master.broker.port"));

        try (Socket socket = NetworkHelper.initConnection(masterBrokerIp, masterBrokerPort)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(this);
            masterBroker = (Broker) objectInputStream.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            LogHelper.error(String.format(PropertiesHelper.getProperty("broker.master.node.required"), masterBrokerIp, masterBrokerPort));
            System.exit(SystemExitCodes.MASTER_NOT_FOUND_ERROR.getCode());
        }

        System.out.println("getMasterBroker() :: Method Returned Master Broker");
        return masterBroker;
    }
}
