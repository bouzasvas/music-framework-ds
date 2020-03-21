package gr.aueb.ds.music.framework.nodes;

import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.Value;

public interface Consumer extends Node {
    void register(Broker broker, ArtistName artistName);
    void disconnect(Broker broker, ArtistName artistName);
    void playData(ArtistName artistName, Value value);
}
