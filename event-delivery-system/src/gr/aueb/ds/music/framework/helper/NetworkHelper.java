package gr.aueb.ds.music.framework.helper;

import gr.aueb.ds.music.framework.commons.ProgramArguments;
import gr.aueb.ds.music.framework.model.network.Connection;
import gr.aueb.ds.music.framework.model.network.Ping;
import gr.aueb.ds.music.framework.model.network.Pong;

import java.io.*;
import java.net.*;

public class NetworkHelper {

    public static String getCurrentIpAddress() {
        boolean onlineMode = ProgramArguments.containsArg("--online");

        String ip;
        try {
            if (onlineMode) {
                ip = getRemoteIp();
            }
            else {
                ip = getLocalIp();
            }
        }
        catch (Exception ex) {
            System.err.println("NetworkHelper :: getCurrentIpAddress :: Could not Retrieve IP Adress of Host.. Returning localhost");
            ip = InetAddress.getLoopbackAddress().getHostAddress();
        }

        return ip;
    }

    private static String getLocalIp() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);

        return socket.getLocalAddress().getHostAddress();
    }

    private static String getRemoteIp() throws IOException {
        URL whatsMyIp = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatsMyIp.openStream()));

        return in.readLine();
    }

    public static ServerSocket initServer(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        return server;
    }

    public static Socket initConnection(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        return socket;
    }

    public static <T,U> U doObjectRequest(Connection connection, T requestObject) throws IOException, ClassNotFoundException {
        ObjectOutputStream os = connection.getOs();
        ObjectInputStream is = connection.getIs();

        os.writeObject(requestObject);

        return (U) is.readObject();
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
