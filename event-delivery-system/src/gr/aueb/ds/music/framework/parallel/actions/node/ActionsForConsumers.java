package gr.aueb.ds.music.framework.parallel.actions.node;

import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
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
        Broker masterBroker = this.broker.getBrokers()
                .stream()
                .filter(br -> ((NodeAbstractImplementation) br).isMasterBroker())
                .findFirst()
                .orElse(null);

        // TODO - Add Consumer to all Brokers?

        try {
            this.objectOutputStream.writeObject(masterBroker);
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
