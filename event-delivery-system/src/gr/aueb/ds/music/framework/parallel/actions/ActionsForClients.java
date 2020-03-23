package gr.aueb.ds.music.framework.parallel.actions;

import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Publisher;

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
            performServerClientCommunication();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    private void performServerClientCommunication() throws IOException, ClassNotFoundException {
        // Continuous Communication
        while (!this.socket.isClosed()) {
            Object receivedObject = this.objectInputStream.readObject();

            if (receivedObject instanceof Broker) {
                Action<Broker> brokerAction = new ActionsForBrokers(this);
                Broker clientBroker = (Broker) receivedObject;

                brokerAction.act(clientBroker);
            } else if (receivedObject instanceof Consumer) {
                Action<Consumer> consumerAction = new ActionsForConsumers();
                Consumer consumer = (Consumer) receivedObject;

                consumerAction.act(consumer);
            } else if (receivedObject instanceof Publisher) {
                Action<Publisher> publisherAction = new ActionsForPublishers();
                Publisher publisher = (Publisher) receivedObject;

                publisherAction.act(publisher);
            } else {
                // TODO -- Error Handling
            }
        }
    }
}
