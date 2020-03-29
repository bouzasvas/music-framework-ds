package gr.aueb.ds.music.framework.nodes.api;

import gr.aueb.ds.music.framework.error.PublisherNotFoundException;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.network.Connection;
import gr.aueb.ds.music.framework.nodes.impl.BrokerImplementation;

import java.util.ArrayList;
import java.util.List;

public interface Broker extends Node {
    void calculateKeys();
    Publisher acceptConnection(Publisher publisher);
    Consumer acceptConnection(Consumer consumer);
    void notifyPublisher(String artistName) throws PublisherNotFoundException;
    List<MusicFile> pull(ArtistName artistName) throws PublisherNotFoundException;

    BrokerImplementation.BrokerIndicator getBrokerIndicator();
    Connection getPublisherConnection();
}
