package gr.aueb.ds.music.framework.parallel.actions.node;

import gr.aueb.ds.music.framework.model.enums.BrokerIndicator;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;
import gr.aueb.ds.music.framework.requests.NodeRequest;

import java.io.IOException;

public class ActionsForBrokers extends ActionImplementation implements Action<NodeRequest> {

    private ActionsForBrokers() {
    }

    public ActionsForBrokers(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void act(NodeRequest nodeRequest) {
        Broker clientBroker = nodeRequest.getBroker();
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
