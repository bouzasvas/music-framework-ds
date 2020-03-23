package gr.aueb.ds.music.framework.helper;

import gr.aueb.ds.music.framework.model.network.Ping;
import gr.aueb.ds.music.framework.model.network.Pong;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkHelper {

    public static String getCurrentIpAddress() {
        String ip;
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);

            ip = socket.getLocalAddress().getHostAddress();
        }
        catch (Exception ex) {
            System.err.println("NetworkHelper :: getCurrentIpAddress :: Could not Retrieve IP Adress of Host.. Returning localhost");
            ip = InetAddress.getLoopbackAddress().getHostAddress();
        }

        return ip;
    }

    public static ServerSocket initServer(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        return server;
    }

    public static Socket initConnection(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        return socket;
    }

    public static boolean checkIfHostIsAlive(String host, int port) throws IOException {
        Socket socket = NetworkHelper.initConnection(host, port);

        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

        // Send a Ping Object & Wait for a Pong Response
        os.writeObject(new Ping());
        Pong pong = null;
        try {
            pong = (Pong) is.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("NetworkHelper :: checkIfHostIsAlive :: Error while Casting Received Object");
        }
        socket.close();

        return pong != null && pong.getMsg().equals("pong");
    }
}
