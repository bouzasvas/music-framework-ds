package gr.aueb.ds.music.framework.model;

import java.io.Serializable;

public class NodeDetails implements Serializable {
    private static final long serialVersionUID = 7581331141467710240L;

    private String name;
    private String ipAddress;
    private int port;

    // For Publisher
    private String[] artistRange;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String[] getArtistRange() {
        return artistRange;
    }

    public void setArtistRange(String[] artistRange) {
        this.artistRange = artistRange;
    }
}
