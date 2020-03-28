package gr.aueb.ds.music.framework.parallel.actions.request;

import gr.aueb.ds.music.framework.error.PublisherNotFoundException;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;
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
            // Get the music files from the appropriate Publisher
            try {
                List<MusicFile> musicFiles = this.broker.pull(request);

                this.objectOutputStream.writeObject(musicFiles);
            } catch (PublisherNotFoundException e) {
                LogHelper.errorWithParams(this.broker, PropertiesHelper.getProperty("broker.publisher.not.found"), request.getArtistName());

                sendErrorResponseToConsumer(e);
            } catch (IOException e) {
                LogHelper.error(this.broker, PropertiesHelper.getProperty("broker.consumer.send.music.list.failed"));
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

    private void sendErrorResponseToConsumer(PublisherNotFoundException e) {
        try {
            this.objectOutputStream.writeObject(e);
        } catch (IOException ex) {
            LogHelper.error(broker, PropertiesHelper.getProperty("broker.consumer.send.error.failed"));
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
