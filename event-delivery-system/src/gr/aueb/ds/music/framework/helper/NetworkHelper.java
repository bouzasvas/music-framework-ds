package gr.aueb.ds.music.framework.helper;

import java.io.IOException;
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
}
