package model;

public class ServerDetail {
    private String hostName;
    private Integer port;
    private String iconName;

    public ServerDetail(String hostName, Integer port, String iconName) {
        this.hostName = hostName;
        this.port = port;
        this.iconName = iconName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    @Override
    public String toString() {
        return hostName + " - " + "Port: " + port;
    }
}
