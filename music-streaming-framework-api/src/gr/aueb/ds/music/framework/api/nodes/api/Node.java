package gr.aueb.ds.music.framework.api.nodes.api;

import gr.aueb.ds.music.framework.api.model.NodeDetails;

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
