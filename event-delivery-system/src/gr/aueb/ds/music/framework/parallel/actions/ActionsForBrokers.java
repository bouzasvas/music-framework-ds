package gr.aueb.ds.music.framework.parallel.actions;

import gr.aueb.ds.music.framework.nodes.api.Broker;

public class ActionsForBrokers extends ActionImplementation implements Action<Broker> {

    private ActionsForBrokers() {
    }

    public ActionsForBrokers(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void act(Broker clientBroker) {

    }
}
