package gr.aueb.ds.music.framework.model.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    private Socket socket;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    public Connection() {
    }

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setSoTimeout(100000);

        this.os = new ObjectOutputStream(this.socket.getOutputStream());
        this.is = new ObjectInputStream(this.getSocket().getInputStream());
    }

    public void close() throws IOException {
        this.os.close();
        this.is.close();
        this.socket.close();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getIs() {
        return is;
    }

    public void setIs(ObjectInputStream is) {
        this.is = is;
    }

    public ObjectOutputStream getOs() {
        return os;
    }

    public void setOs(ObjectOutputStream os) {
        this.os = os;
    }
}
