package gr.aueb.ds.music.framework.parallel.actions.request;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActionsForConsumerRequest extends ActionImplementation implements RequestAction<ArtistName> {

    public ActionsForConsumerRequest() { }

    public ActionsForConsumerRequest(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void handleRequest(ArtistName request) {
        if (request.isDiscoveryRequest()) {
            findTheAppropriateBroker(request);
        }
        else {
            // Dummy Return Songs List
            List<MusicFile> musicFiles = new ArrayList<>();

            MusicFile mf1 = new MusicFile();
            mf1.setArtistName("Pantelis Pantelidis");
            mf1.setTrackName("De Tairiazete soy lew");
            mf1.setAlbumInfo("Alkoolikes Oi Nyxtes");
            mf1.setGenre("Skyladika");

            MusicFile mf2 = new MusicFile();
            mf2.setArtistName("Pantelis Pantelidis");
            mf2.setTrackName("Skoypise ta podia soy kai perase");
            mf2.setAlbumInfo("Ouranio Toxo");
            mf2.setGenre("Skyladika");

            musicFiles.add(mf1);
            musicFiles.add(mf2);

            try {
                this.objectOutputStream.writeObject(musicFiles);
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }
        }
    }

    private void findTheAppropriateBroker(ArtistName request) {
        Broker suitableBroker = null;

        try {
            // Calculate Keys of Each Registerer Broker
            this.broker.getBrokers().forEach(Broker::calculateKeys);

            // Get the appropriate Broker to send as Response
            suitableBroker = this.broker.getBrokers()
                    .stream()
                    // TODO filtering
                    // .filter()
                    .findAny().orElse(null);

            this.objectOutputStream.writeObject(suitableBroker);
            this.closeConnectionIfBrokerIsNotMaster(suitableBroker);
        }
        catch (IOException ex) {
            LogHelper.error(this.broker, String.format(PropertiesHelper.getProperty("consumer.request.artistName.error"), suitableBroker, request));
        }
    }

    private void closeConnectionIfBrokerIsNotMaster(Broker suitableBroker) {
        if (!this.broker.equals(suitableBroker)) {
            try {
                this.objectOutputStream.close();
                this.objectInputStream.close();

                this.socket.close();
            } catch (IOException e) {
                LogHelper.error(this.broker, PropertiesHelper.getProperty("consumer.request.artistName.disconnect"));
            }
        }
    }
}
