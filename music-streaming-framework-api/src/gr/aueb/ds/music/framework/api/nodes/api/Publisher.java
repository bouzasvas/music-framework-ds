package gr.aueb.ds.music.framework.api.nodes.api;

import gr.aueb.ds.music.framework.api.model.dto.ArtistName;
import gr.aueb.ds.music.framework.api.model.dto.Value;

public interface Publisher extends Node {
    Broker hashTopic(ArtistName artistName);
    void push(ArtistName artistName, Value value);
    void notifyFailure(Broker broker);

    boolean isPublisherDown();
}
