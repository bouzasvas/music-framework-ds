package gr.aueb.ds.music.framework.nodes.api;

import gr.aueb.ds.music.framework.model.NodeDetails;

import java.io.IOException;
import java.util.List;

public interface Node {
    void init() throws IOException;

    List<Broker> getBrokers();
    NodeDetails getNodeDetails();

    void connect();
    void disconnect();
    void updateNodes();
}
