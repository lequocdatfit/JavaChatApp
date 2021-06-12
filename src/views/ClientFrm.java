package views;

import Thread.WriteThread;
import Thread.ReadThread;
import model.User;
import model.UserRendered;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientFrm extends JFrame {
    private JPanel rootPanel;
    private JTextArea MessageArea;
    private JTextArea txtMessage;
    private JButton btnSend;
    private JList jListUsers;
    private ServerListFrm serverList;
    private DefaultListModel<User> usersListModel;

    private Socket s;
    private User currentUser;
    private ArrayList<User> listUser;

    public ClientFrm(Frame serverList, Socket s, User user) {
        super();
        setTitle("Chat app");
        setContentPane(rootPanel);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverList = (ServerListFrm) serverList;
        currentUser = user;

        usersListModel = new DefaultListModel<>();
        jListUsers.setModel(usersListModel);
        jListUsers.setCellRenderer(new UserRendered());

        Thread readThread = new Thread(new ReadThread(this, s));
        readThread.start();
        Thread writeThread = new Thread(new WriteThread(this, s, currentUser));
        writeThread.start();

    }

    public void execute() {
        try {
            s = new Socket("localhost", 3000);
            System.out.println("Connected to server");
            Thread readThread = new Thread(new ReadThread(this, s));
            readThread.start();
            Thread writeThread = new Thread(new WriteThread(this, s, currentUser));
            writeThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateListUsers(ArrayList<User> users) {
        listUser = users;

        for (User u : listUser) {
            usersListModel.addElement(u);
        }
    }

    public void setUserOnline(User u) {
        for (User user : listUser) {
            if(user.getId().equals(u.getId())) {
                user.setConnected(true);
            }
        }
    }
}
