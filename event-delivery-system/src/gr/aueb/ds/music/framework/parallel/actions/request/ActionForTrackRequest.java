package gr.aueb.ds.music.framework.parallel.actions.request;

import gr.aueb.ds.music.framework.helper.NetworkHelper;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.model.network.Connection;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ActionForTrackRequest extends ActionImplementation implements RequestAction<Value> {

    public ActionForTrackRequest(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void handleRequest(Value request) {
        Connection publisherConnection = this.broker.getPublisherConnection();

        try {
            ObjectInputStream publisherConnectionIs = publisherConnection.getIs();

            MusicFile musicFile = NetworkHelper.doObjectRequest(publisherConnection, request);
            while ((musicFile = (MusicFile)publisherConnectionIs.readObject()) != null) {
                this.objectOutputStream.writeObject(musicFile);
            }
            this.objectOutputStream.writeObject(null);

        } catch (IOException | ClassNotFoundException e) {
            // TODO -- Logging
            e.printStackTrace();
        }
    }
}
