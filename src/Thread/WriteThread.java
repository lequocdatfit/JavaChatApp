package Thread;

import model.Message;
import model.User;
import views.ClientFrm;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteThread implements Runnable{
    private Socket socket;
    private ClientFrm client;
    private ObjectOutputStream writer;
    private User user;

    public WriteThread(ClientFrm client, Socket s, User user) {
        this.client = client;
        this.socket = s;
        this.user = user;

    }

    @Override
    public void run() {
        Message sessionEvent = new Message("SESSION", user);
        try {
            writer = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            // tell server you're online
            writer.writeObject(sessionEvent);
            writer.flush();
            do {
                //System.out.println("Hello");
            } while (true);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
