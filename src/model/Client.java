package model;

import java.io.Serializable;

public class Client implements Serializable {
    private String ipAddress;
    private Integer port;
    private String username;

    public Client(String ipAddress, Integer port, String username) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
