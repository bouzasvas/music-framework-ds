package gr.aueb.ds.music.framework.parallel.actions.request;

import gr.aueb.ds.music.framework.error.PublisherNotFoundException;
import gr.aueb.ds.music.framework.helper.HashingHelper;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
            suitableBroker = findHashedBrokerBasedOnArtist(request.getArtistName());

            this.objectOutputStream.writeObject(suitableBroker);
            this.closeConnectionIfBrokerIsNotMaster(suitableBroker);
        } catch (IOException ex) {
            LogHelper.error(this.broker, String.format(PropertiesHelper.getProperty("consumer.request.artistName.error"), suitableBroker, request));
        }
    }

    private Broker findHashedBrokerBasedOnArtist(String artistName) {
        BigInteger artistHash = HashingHelper.hashText(artistName);

        // Construct Map of Broker Hash as Key and Broker as Value
        Map<BigInteger, Broker> brokerHashes = this.broker
                .getBrokers()
                .stream()
                .collect(Collectors.toMap(
                        br -> br.getNodeDetails().getBrokerHash(),
                        br -> br,
                        (v1, v2) -> v1,
                        TreeMap::new));

        BigInteger minimumHashing = brokerHashes.keySet().stream().min(Comparator.naturalOrder()).orElse(BigInteger.valueOf(Long.MIN_VALUE));
        BigInteger maximumHashing = brokerHashes.keySet().stream().max(Comparator.naturalOrder()).orElse(BigInteger.valueOf(Long.MAX_VALUE));

        // If Artist Hashing bigger than Maximum Hashing return Broker with Minimum Hashing
        Broker suitableBroker = null;
        if (artistHash.compareTo(maximumHashing) > 0) {
            suitableBroker = brokerHashes.get(minimumHashing);
        }
        else {
            // Init rightHashing with Min Hashing value
            AtomicReference<BigInteger> rightHashing = new AtomicReference<>(minimumHashing);
            brokerHashes.keySet().forEach(k -> rightHashing.set(k.min(artistHash)));

            suitableBroker = brokerHashes.get(rightHashing.get());
        }

        return suitableBroker;
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
