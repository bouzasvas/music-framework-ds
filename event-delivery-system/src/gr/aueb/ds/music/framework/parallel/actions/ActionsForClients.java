package gr.aueb.ds.music.framework.parallel.actions;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.api.model.dto.ArtistName;
import gr.aueb.ds.music.framework.api.model.dto.MusicData;
import gr.aueb.ds.music.framework.api.model.dto.Value;
import gr.aueb.ds.music.framework.api.model.network.ObjectOverNetwork;
import gr.aueb.ds.music.framework.api.model.network.Ping;
import gr.aueb.ds.music.framework.api.nodes.api.Broker;
import gr.aueb.ds.music.framework.api.nodes.api.Consumer;
import gr.aueb.ds.music.framework.api.nodes.api.Node;
import gr.aueb.ds.music.framework.api.nodes.api.Publisher;
import gr.aueb.ds.music.framework.parallel.actions.network.ActionForLiveness;
import gr.aueb.ds.music.framework.parallel.actions.node.Action;
import gr.aueb.ds.music.framework.parallel.actions.node.ActionsForBrokers;
import gr.aueb.ds.music.framework.parallel.actions.node.ActionsForConsumers;
import gr.aueb.ds.music.framework.parallel.actions.node.ActionsForPublishers;
import gr.aueb.ds.music.framework.parallel.actions.request.*;

import java.io.IOException;
import java.net.Socket;

public class ActionsForClients extends ActionImplementation implements Runnable {

    private ActionsForClients() {
        super();
    }

    public ActionsForClients(Broker broker, Socket socket) {
        super(broker, socket);
    }

    public ActionsForClients(Publisher publisher, Socket socket) {
        super(publisher, socket);
    }

    @Override
    public void run() {
        try {
            LogHelper.info((this.broker != null ? this.broker : this.publisher), PropertiesHelper.getProperty("broker.connection.accept"));
            performServerClientCommunication();
        } catch (IOException | ClassNotFoundException ex) {
            LogHelper.error(this.broker, ex.getMessage());
        }

    }

    private void performServerClientCommunication() throws IOException, ClassNotFoundException {
        // Continuous Communication
        while (!this.socket.isClosed()) {
            Object receivedObject = this.objectInputStream.readObject();

            // Node Actions
            if (receivedObject instanceof Node) {
                Node node = (Node) receivedObject;
                manageMessageFromNode(node);
            }
            // Network Actions
            else if (receivedObject instanceof ObjectOverNetwork) {
                Ping ping = (Ping) receivedObject;
                new ActionForLiveness(this).act(ping);
            }
            else if (receivedObject instanceof MusicData) {
                MusicData musicData = (MusicData) receivedObject;
                manageMusicDataFromNetwork(musicData);
            }
            // Unknown Object
            else {
                // TODO -- Error Handling
            }
        }
    }

    private void manageMessageFromNode(Node node) {
        if (node instanceof Broker) {
            Action<Broker> brokerAction = new ActionsForBrokers(this);
            Broker clientBroker = (Broker) node;

            brokerAction.act(clientBroker);
        } else if (node instanceof Consumer) {
            Action<Consumer> consumerAction = new ActionsForConsumers(this);
            Consumer consumer = (Consumer) node;

            consumerAction.act(consumer);
        } else if (node instanceof Publisher) {
            Action<Publisher> publisherAction = new ActionsForPublishers(this);
            Publisher publisher = (Publisher) node;

            publisherAction.act(publisher);
        }
    }

    private void manageMusicDataFromNetwork(MusicData musicData) {
        Object musicObject = musicData.get();

        if (musicObject instanceof ArtistName) {
            ArtistName artistName = (ArtistName) musicObject;

            // Request - Response based on Request source (Publisher / Broker)
            if (this.publisher == null) {
                new ActionsForConsumerRequest(this).handleRequest(artistName);
            }
            else {
                new ActionsForConsumerRequestInPublisher(this).handleRequest(artistName);
            }
        }
        else if (musicObject instanceof Value) {
            Value value = (Value) musicObject;

            if (this.publisher == null) {
                RequestAction<Value> requestAction = new ActionForTrackRequest(this);
                requestAction.handleRequest(value);
            }
            else {
                RequestAction<Value> requestAction = new ActionForTrackRequestInPublisher(this);
                requestAction.handleRequest(value);
            }
        }
    }
}
