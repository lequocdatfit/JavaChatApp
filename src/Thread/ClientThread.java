package Thread;

import DAO.DAO;
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

                    // notify existing users
                    Message userConnEvent = new Message("USER_CONN", currentUser);
                    broadcastAllExcludeCurrentSocket(userConnEvent);

                    // fetch all user
                    ArrayList<User> list_users = fetchAllUsers();
                    // emit event fetchUsers
                    Message emitFetchUsers = new Message("FETCH_USERS", list_users);
                    this.sendMessage(emitFetchUsers);
                    serverFrm.ServerLogAppend("Fetch_user\n");
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
            out.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public ArrayList<User> fetchAllUsers() {
        ArrayList<User> ls = new DAO().getAllUsers();
        //ls.forEach((user -> user.setConnected(true)));
        for (int i=0; i< ls.size(); i++) {
            User u = ls.get(i);
            u.setConnected(false);
            for (int j=0; j < clients.size(); j++) {
                ClientThread client = clients.get(j);
                if(client.getUser().getId().equals(u.getId())) {
                    u.setConnected(true);
                    break;
                }
            }
        }
        return ls;
    }

    public User getUser() {
        return this.currentUser;
    }
}
