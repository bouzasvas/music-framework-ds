package gr.aueb.ds.music.framework.nodes.api;

import java.io.IOException;
import java.util.List;

public interface Node {
    void init() throws IOException;

    List<Broker> getBrokers();

    void connect();
    void disconnect();
    void updateNodes();
}
