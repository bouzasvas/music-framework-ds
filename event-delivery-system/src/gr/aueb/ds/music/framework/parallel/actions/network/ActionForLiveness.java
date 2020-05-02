package gr.aueb.ds.music.framework.parallel.actions.network;

import gr.aueb.ds.music.framework.api.model.network.Ping;
import gr.aueb.ds.music.framework.api.model.network.Pong;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;

public class ActionForLiveness extends ActionImplementation implements NetworkActions<Ping> {

    public ActionForLiveness(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void act(Ping objectOverNetwork) {
        try {
            this.objectOutputStream.writeObject(new Pong());
        } catch (IOException e) {
            System.err.println("ActionForLiveness :: act :: Error while Transmitting Pong Msg");
        }
        finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                System.err.println("ActionForLiveness :: act :: Error while closing socket");
            }
        }
    }
}
