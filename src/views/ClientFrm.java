package views;

import Thread.ReadThread;
import Thread.WriteThread;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class ClientFrm extends JFrame {
    private JPanel rootPanel;
    private JTextArea MessageArea;
    private JTextArea txtMessage;
    private JButton btnSend;
    private JList list1;
    private ServerListFrm serverList;

    private Socket s;
    private User currentUser;

    public ClientFrm(Frame serverList, Socket s, User user) {
        super();
        serverList = (ServerListFrm) serverList;
        currentUser = user;
        setTitle("Chat app");
        setContentPane(rootPanel);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
}
