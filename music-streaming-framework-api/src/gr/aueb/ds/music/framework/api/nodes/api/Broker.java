package gr.aueb.ds.music.framework.api.nodes.api;

import gr.aueb.ds.music.framework.api.error.PublisherNotFoundException;
import gr.aueb.ds.music.framework.api.model.dto.ArtistName;
import gr.aueb.ds.music.framework.api.model.dto.MusicFile;
import gr.aueb.ds.music.framework.api.model.enums.BrokerIndicator;
import gr.aueb.ds.music.framework.api.model.network.Connection;

import java.util.List;

public interface Broker extends Node {
    void calculateKeys();
    Publisher acceptConnection(Publisher publisher);
    Consumer acceptConnection(Consumer consumer);
    void notifyPublisher(String artistName) throws PublisherNotFoundException;
    List<MusicFile> pull(ArtistName artistName) throws PublisherNotFoundException;

    BrokerIndicator getBrokerIndicator();
    Connection getPublisherConnection();
}
