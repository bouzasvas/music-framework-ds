package gr.aueb.ds.music.android.lalapp.request;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.enums.NodeType;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Node;
import gr.aueb.ds.music.framework.requests.NodeRequest;

public abstract class NodeAbstractImplementation implements Node, Serializable {
    private final static long serialVersionUID = 5792977548048762220L;

    private final static long ONE_MINUTE_MILLIS = 60000;

    protected List<Broker> brokers = new ArrayList<>();
    protected NodeDetails nodeDetails;

    protected Map<String, String> applicationSettings;

    public NodeAbstractImplementation() { }

    public NodeAbstractImplementation(Map<String, ?> applicationSettings) {
        this.applicationSettings = new HashMap<>();

        for (Map.Entry<String, ?> entry : applicationSettings.entrySet()) {
            this.applicationSettings.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

//    public NodeAbstractImplementation(boolean enableTimer) {
//        if (enableTimer) {
//            // Enable Timer after 1 Minute of Initial Execution
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.MINUTE, 2);
//
//            Date date = cal.getTime();
//            new Timer().scheduleAtFixedRate(new UpdateNodesTimerTask(), date, ONE_MINUTE_MILLIS);
//        }
//    }

    @Override
    public void updateNodes() {
//        List<Broker> connectedBrokers = getBrokers();
//
//        List<Broker> brokersToBeRemoved = new ArrayList<>();
//
//        // Check Connectivity with Brokers
//        for (Broker broker : connectedBrokers) {
//            if (!broker.equals(this)) {
//                try {
//                    String ip = broker.getNodeDetails().getIpAddress();
//                    int port = broker.getNodeDetails().getPort();
//
//                    NetworkHelper.checkIfHostIsAlive(ip, port);
//                } catch (IOException e) {
////                    LogHelper.errorWithParams(this, PropertiesHelper.getProperty("broker.liveness.failed"), broker.getNodeDetails().getName());
//                    brokersToBeRemoved.add(broker);
//                }
//            }
//        }
//
//        for (Broker broker : brokersToBeRemoved) broker.disconnect();
    }

    @Override
    public List<Broker> getBrokers() {
        // If Broker is not Master Broker then Retrieve Brokers from Master
        if (!this.isMasterBroker()) {
//            Broker masterBroker = this.getMasterBroker();
//            this.setBrokers(masterBroker.getBrokers());
        }

        return this.brokers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return  true;
        if (obj instanceof NodeAbstractImplementation) {
            NodeAbstractImplementation other = (NodeAbstractImplementation) obj;

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
    public boolean isMasterBroker() {
        return this.getNodeDetails().getPort() == Integer.parseInt(applicationSettings.get("master_port"));
    }

    protected NodeDetails getMasterBrokerDetails() {
        NodeDetails masterBrokerDetails = null;

        String masterBrokerIp = applicationSettings.get("master_ip");
        int masterBrokerPort = Integer.parseInt(applicationSettings.get("master_port"));

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(masterBrokerIp, masterBrokerPort), 10000);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            NodeRequest nodeRequest = new NodeRequest(NodeType.CONSUMER, this.getNodeDetails());
            objectOutputStream.writeObject(nodeRequest);
            masterBrokerDetails = (NodeDetails) objectInputStream.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return masterBrokerDetails;
    }

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

    // Nested TimerTask class
//    private class UpdateNodesTimerTask extends TimerTask {
//        @Override
//        public void run() {
////            LogHelper.info(NodeAbstractImplementation.this,
////                    String.format(PropertiesHelper.getProperty("nodes.timer.executed"), new Date().toString()));
//
//            updateNodes();
//        }
//    }
}
