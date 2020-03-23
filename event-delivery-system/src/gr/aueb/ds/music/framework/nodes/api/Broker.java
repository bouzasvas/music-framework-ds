package gr.aueb.ds.music.framework.nodes.api;

import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.nodes.impl.BrokerImplementation;

import java.util.ArrayList;
import java.util.List;

public interface Broker extends Node {
    void calculateKeys();
    Publisher acceptConnection(Publisher publisher);
    Consumer acceptConnection(Consumer consumer);
    // TODO -- Check param name
    void notifyPublisher(String artistName);
    void pull(ArtistName artistName);

    BrokerImplementation.BrokerIndicator getBrokerIndicator();
}
