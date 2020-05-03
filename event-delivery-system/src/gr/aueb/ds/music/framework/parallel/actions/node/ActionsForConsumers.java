package gr.aueb.ds.music.framework.parallel.actions.node;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.NodeDetails;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.impl.NodeAbstractImplementation;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;

public class ActionsForConsumers extends ActionImplementation implements Action<Consumer> {

    public ActionsForConsumers() {
        super();
    }

    public ActionsForConsumers(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void act(Consumer consumer) {
        NodeDetails masterBrokerDetails = this.broker.getBrokers()
                .stream()
                .filter(br -> ((NodeAbstractImplementation) br).isMasterBroker())
                .findFirst()
                .map(Broker::getNodeDetails)
                .orElse(null);

        // TODO - Add Consumer to all Brokers?

        try {
            this.objectOutputStream.writeObject(masterBrokerDetails);
        } catch (IOException ex) {
            LogHelper.error(this.broker, String.format(PropertiesHelper.getProperty("broker.return.master.to.consumer"), consumer.getNodeDetails().getName()));
        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                LogHelper.error(this.broker, String.format(PropertiesHelper.getProperty("broker.return.master.to.consumer.close.error"), consumer.getNodeDetails().getName()));
            }
        }
    }
}
