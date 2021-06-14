package Thread;

import DAO.DAO;
import model.Client;
import model.Message;
import model.User;
import views.ServerFrm;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
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

                    String address = ((InetSocketAddress) client.getRemoteSocketAddress()).getAddress().toString();
                    Client cl = new Client(address, this.client.getPort(), currentUser.getName());
                    serverFrm.addNewClient(cl);
                    serverFrm.updateClientTable();

                    // notify existing users
                    Message userConnEvent = new Message("USER_CONN", currentUser);
                    broadcastAllExcludeCurrentSocket(userConnEvent);

                    // fetch all user
                    ArrayList<User> list_users = fetchAllUsers();
                    // emit event fetchUsers
                    Message emitFetchUsers = new Message("FETCH_USERS", list_users);
                    this.sendMessage(emitFetchUsers);
                    serverFrm.ServerLogAppend("Fetch_user\n");
                } else if(request.getType().equals("PRIVATE_MESSAGE")) {
                    // forward the private message to the right recipient (and to the other tab of sender)
                    String recipient = request.getTo();
                    this.forwardPrivateMessage(recipient, request);
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

    public void forwardPrivateMessage(String userId, Message serverMessage) {
        for (ClientThread client : clients) {
            if(client.getUser().getId().equals(userId)) {
                client.sendMessage(serverMessage);
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
        /*ArrayList<User> ls = new DAO().getAllUsers();
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
        return ls; */
        ArrayList<User> ls = new ArrayList<>();
        for (ClientThread cl : clients) {
            User u = cl.getUser();
            u.setConnected(true);
            ls.add(u);
        }
        return ls;
    }

    public User getUser() {
        return this.currentUser;
    }
}
