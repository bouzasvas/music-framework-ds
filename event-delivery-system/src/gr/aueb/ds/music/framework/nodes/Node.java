package gr.aueb.ds.music.framework.nodes;

import java.util.ArrayList;
import java.util.List;

public interface Node {
    List<Broker> brokers = new ArrayList<>();

    void init(int port);

    default List<Broker> getBrokers() {
        return brokers;
    }

    void connect();
    void disconnect();
    void updateNodes();
}
