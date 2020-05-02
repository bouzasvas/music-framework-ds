package gr.aueb.ds.music.framework.parallel.actions.node;

import gr.aueb.ds.music.framework.api.model.enums.BrokerIndicator;
import gr.aueb.ds.music.framework.api.nodes.api.Broker;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;

public class ActionsForBrokers extends ActionImplementation implements Action<Broker> {

    private ActionsForBrokers() {
    }

    public ActionsForBrokers(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void act(Broker clientBroker) {
        try {
            // Send Existing Master Broker
            this.objectOutputStream.writeObject(this.broker);

            // Update Master Broker Internal Brokers list
            if (clientBroker.getBrokerIndicator().equals(BrokerIndicator.TO_ADD)) {
                if (!this.broker.getBrokers().contains(clientBroker)) {
                    this.broker.getBrokers().add(clientBroker);
                }
            } else if (clientBroker.getBrokerIndicator().equals(BrokerIndicator.TO_DELETE)) {
                this.broker.getBrokers().remove(clientBroker);
            }


        } catch (IOException e) {
            System.err.println("ActionsForBrokers :: act :: Error while Reading from Broker Client");
        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                System.err.println("ActionsForBrokers :: act :: Error while closing socket");
            }
        }
    }
}
