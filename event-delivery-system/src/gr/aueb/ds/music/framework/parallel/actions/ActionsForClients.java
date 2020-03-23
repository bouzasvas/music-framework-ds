package gr.aueb.ds.music.framework.parallel.actions;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.network.Ping;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Node;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.parallel.actions.network.ActionForLiveness;

import java.io.IOException;
import java.net.Socket;

public class ActionsForClients extends ActionImplementation implements Runnable {

    private ActionsForClients() {
        super();
    }

    public ActionsForClients(Broker broker, Socket socket) {
        super(broker, socket);
    }

    @Override
    public void run() {
        try {
            LogHelper.info(this.broker, PropertiesHelper.getProperty("broker.connection.accept"));
            performServerClientCommunication();
        } catch (IOException | ClassNotFoundException ex) {
            LogHelper.error(this.broker, ex.getMessage());
        }

    }

    private void performServerClientCommunication() throws IOException, ClassNotFoundException {
        // Continuous Communication
        while (!this.socket.isClosed()) {
            Object receivedObject = this.objectInputStream.readObject();

            if (receivedObject instanceof Node) {
                Node node = (Node) receivedObject;
                manageMessageFromNode(node);
            }
            else if (receivedObject instanceof Ping) {
                Ping ping = (Ping) receivedObject;
                new ActionForLiveness(this).act(ping);
            }
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
            Action<Consumer> consumerAction = new ActionsForConsumers();
            Consumer consumer = (Consumer) node;

            consumerAction.act(consumer);
        } else if (node instanceof Publisher) {
            Action<Publisher> publisherAction = new ActionsForPublishers();
            Publisher publisher = (Publisher) node;

            publisherAction.act(publisher);
        }
    }
}
