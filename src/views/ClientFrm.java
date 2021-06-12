package views;

import Thread.WriteThread;
import Thread.ReadThread;
import model.Message;
import model.MessageStore;
import model.User;
import model.UserRendered;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientFrm extends JFrame {
    private JPanel rootPanel;
    private JTextArea MessageArea;
    private JTextArea txtMessage;
    private JButton btnSend;
    private JList jListUsers;
    private JScrollPane scrollPanelMsg;
    private ServerListFrm serverList;
    private DefaultListModel<User> usersListModel;

    private Socket s;
    private User currentUser;
    private ArrayList<User> listUser;
    private ObjectOutputStream writer;

    public ClientFrm(Frame serverList, Socket s, User user) {
        super();
        setTitle("Chat app");
        setContentPane(rootPanel);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.serverList = (ServerListFrm) serverList;
        currentUser = user;
        try {
            writer = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        scrollPanelMsg.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        usersListModel = new DefaultListModel<>();
        jListUsers.setModel(usersListModel);
        jListUsers.setCellRenderer(new UserRendered());

        jListUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println("state change!");
                MessageArea.setText("");
                // find message for selected user
                String selectedUserId = ((User) jListUsers.getSelectedValue()).getId();
                Message[] listMessages = MessageStore.findMessageForUser(selectedUserId);
                if(listMessages != null) {
                    for (Message msg: listMessages) {
                        MessageArea.setText((String) msg.getPayload() + "\n");
                    }
                }

            }
        });

        Thread readThread = new Thread(new ReadThread(this, s));
        readThread.start();
        Message sessionEvent = new Message("SESSION", currentUser);
        Thread writeThread = new Thread(new WriteThread(this, s, currentUser, sessionEvent, writer));
        writeThread.start();
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = txtMessage.getText();
                String to = ((User) jListUsers.getSelectedValue()).getId();
                Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, privateMessage, writer));
                privateThread.start();
                txtMessage.setText("");
                MessageArea.append(content + "\n");
                MessageStore.saveMessage(to, privateMessage);
            }
        });


    }

    /*public void execute() {
        try {
            s = new Socket("localhost", 3000);
            System.out.println("Connected to server");
            Thread readThread = new Thread(new ReadThread(this, s));
            readThread.start();
            Thread writeThread = new Thread(new WriteThread(this, s, currentUser, ));
            writeThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


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

    public void onPrivateMessage(Message msg) {
        MessageArea.append((String) msg.getPayload() + "\n");
    }
}
