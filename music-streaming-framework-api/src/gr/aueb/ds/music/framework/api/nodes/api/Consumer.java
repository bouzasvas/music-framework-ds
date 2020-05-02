package gr.aueb.ds.music.framework.api.nodes.api;

import gr.aueb.ds.music.framework.api.model.dto.ArtistName;
import gr.aueb.ds.music.framework.api.model.dto.Value;

public interface Consumer extends Node {
    void register(Broker broker, ArtistName artistName);
    void disconnect(Broker broker, ArtistName artistName);
    void playData(ArtistName artistName, Value value);
}
