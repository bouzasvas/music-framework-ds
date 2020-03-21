package gr.aueb.ds.music.framework.nodes;

import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.Value;

public interface Publisher extends Node {
    Broker hashTopic(ArtistName artistName);
    void push(ArtistName artistName, Value value);
    void notifyFailure(Broker broker);
}
