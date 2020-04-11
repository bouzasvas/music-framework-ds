package gr.aueb.ds.music.framework.parallel.actions.node;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.nodes.impl.NodeAbstractImplementation;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ActionsForPublishers extends ActionImplementation implements Action<Publisher> {

    public ActionsForPublishers(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void act(Publisher publisher) {
        this.broker.acceptConnection(publisher);
        if (((NodeAbstractImplementation) this.broker).isMasterBroker()) {
            this.updateAllBrokers(publisher);

            // Return Master to Publisher
            try {
                this.objectOutputStream.writeObject(this.broker);
            } catch (IOException e) {
                LogHelper.errorWithParams(this.broker, PropertiesHelper.getProperty("publisher.update.brokers.return.master.failed"), publisher.getNodeDetails().getName());
            }
        }

        closeConnection(publisher);
    }

    private void updateAllBrokers(Publisher publisher) {
        // Update all other Broker using Master
        this.broker
                .getBrokers()
                .stream()
                .filter(br -> !((NodeAbstractImplementation)br).isMasterBroker())
                .forEach(broker -> {

                    this.registerPublisherToBroker(publisher, broker.getNodeDetails());
                    broker.acceptConnection(publisher);
                });
    }

    private void registerPublisherToBroker(Publisher publisher, NodeDetails nodeDetails) {
        String brokerIp = nodeDetails.getIpAddress();
        int brokerPort = nodeDetails.getPort();

        try (Socket brokerSocket = new Socket(brokerIp, brokerPort);
             ObjectOutputStream os = new ObjectOutputStream(brokerSocket.getOutputStream());
             ObjectInputStream is = new ObjectInputStream(brokerSocket.getInputStream())) {

            os.writeObject(publisher);

        } catch (IOException e) {
            LogHelper.error(this.broker, String.format(
                    PropertiesHelper.getProperty("publisher.update.brokers.connection.failed"),
                    this.broker.getNodeDetails().getName(),
                    publisher.getNodeDetails().getName()));
        }
    }

    private void closeConnection(Publisher publisher) {
        try {
            this.socket.close();
        } catch (IOException e) {
            LogHelper.error(this.broker, String.format(
                    PropertiesHelper.getProperty("publisher.update.brokers.connection.close.failed"),
                    this.broker.getNodeDetails().getName(),
                    publisher.getNodeDetails().getName()));
        }
    }
}
