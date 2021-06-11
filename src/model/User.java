package model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String nameLogin;
    private String name;
    private String password;
    private Boolean isConnected;

    public User(String id, String nameLogin, String name, String password) {
        this.id = id;
        this.nameLogin = nameLogin;
        this.name = name;
        this.password = password;
    }

    public Boolean getConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }

    public String getNameLogin() {
        return nameLogin;
    }

    public void setNameLogin(String nameLogin) {
        this.nameLogin = nameLogin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
