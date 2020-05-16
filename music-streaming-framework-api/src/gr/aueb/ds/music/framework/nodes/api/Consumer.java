package gr.aueb.ds.music.framework.nodes.api;

import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.Value;

public interface Consumer extends Node {
    void register(NodeDetails brokerDetails, ArtistName artistName);
    void disconnect(Broker broker, ArtistName artistName);
    void playData(ArtistName artistName, Value value);
}
