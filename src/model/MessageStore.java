package model;

import java.util.HashMap;
import java.util.Map;

public class MessageStore {
    private static Map<String, Message[]> data = new HashMap();

    public static Message[] findMessageForUser(String userId) {
        if(data.get(userId) != null) {
            return data.get(userId);
        } else {
            return null;
        }
    }

    public static void saveMessage(String userId, Message msg) {
        if(data.get(userId) != null) {
            Message[] oldMessages = data.get(userId);
            Message[] newMessages = new Message[oldMessages.length + 1];
            System.arraycopy(oldMessages, 0, newMessages, 0, oldMessages.length);
            newMessages[oldMessages.length] = msg;
            data.put(userId, newMessages);
        } else {
            Message[] listMsg = new Message[1];
            listMsg[0] = msg;
            data.put(userId, listMsg);
        }
    }
}
