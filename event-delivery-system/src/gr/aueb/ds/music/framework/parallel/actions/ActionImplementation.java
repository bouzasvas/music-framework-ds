package gr.aueb.ds.music.framework.parallel.actions;

import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Publisher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ActionImplementation {
    protected Publisher publisher;
    protected Broker broker;

    protected Socket socket;

    protected ObjectInputStream objectInputStream;
    protected ObjectOutputStream objectOutputStream;

    public ActionImplementation() {
    }

    public ActionImplementation(ActionImplementation actionImplementation) {
        this.publisher = actionImplementation.publisher;
        this.broker = actionImplementation.broker;
        this.socket = actionImplementation.socket;
        this.objectOutputStream = actionImplementation.objectOutputStream;
        this.objectInputStream = actionImplementation.objectInputStream;
    }

    public ActionImplementation(Broker broker, Socket socket) {
        this.broker = broker;
        this.socket = socket;

        try {
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ActionImplementation(Publisher publisher, Socket socket) {
        this.publisher = publisher;
        this.socket = socket;

        try {
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
