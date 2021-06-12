package model;

import java.io.Serializable;

public class Message implements Serializable {
    private String type;
    private Object payload;
    private String from;
    private String to;

    public Message(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public Message(String type, Object payload, String from, String to) {
        this.type = type;
        this.payload = payload;
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
