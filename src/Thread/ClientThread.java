package Thread;

import model.Message;
import model.User;
import views.ServerFrm;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread implements Runnable{
    private Socket client;
    private ArrayList<ClientThread> clients;
    private User currentUser;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerFrm serverFrm;

    public ClientThread(Frame server, Socket client, ArrayList<ClientThread> clients) throws IOException {
        this.client = client;
        this.clients = clients;
        serverFrm = (ServerFrm) server;
        in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
        out = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            Message request = null;
            do {
                request = (Message) in.readObject();
                if(request.getType().equals("SESSION")) {
                    // get user
                    currentUser = (User) request.getPayload();
                    serverFrm.ServerLogAppend(currentUser.getId());

                    // emit event new user connected to all users.
                    Message userConnEvent = new Message("USER_CONN", currentUser);
                    broadcastAllExcludeCurrentSocket(userConnEvent);

                    // fetch all user

                }
            } while (true);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void broadcastAllExcludeCurrentSocket(Message msg) {
        for (ClientThread aClient : clients) {
            if(!aClient.getUser().getId().equals(this.getUser().getId())) {
                aClient.sendMessage(msg);
            }
        }
    }

    public void sendMessage(Message serverMessage) {
        try {
            out.writeObject(serverMessage);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public User getUser() {
        return this.currentUser;
    }
}
