package gr.aueb.ds.music.framework.nodes;

import gr.aueb.ds.music.framework.model.dto.ArtistName;

import java.util.ArrayList;
import java.util.List;

public interface Broker extends Node {
    List<Consumer> registeredUsers = new ArrayList<>();
    List<Publisher> registeredPublishers = new ArrayList<>();

    void calculateKeys();
    Publisher acceptConnection(Publisher publisher);
    Consumer acceptConnection(Consumer consumer);
    // TODO -- Check param name
    void notifyPublisher(String artistName);
    void pull(ArtistName artistName);
}
