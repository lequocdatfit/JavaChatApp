package views;

import Thread.ReadThread;
import Thread.WriteThread;

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

    public ClientFrm(Frame serverList, Socket s) {
        super();
        serverList = (ServerListFrm) serverList;
        setTitle("Chat app");
        setContentPane(rootPanel);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Thread readThread = new Thread(new ReadThread(this, s));
        readThread.start();
        Thread writeThread = new Thread(new WriteThread(this, s));
        writeThread.start();
    }

    public void execute() {
        try {
            s = new Socket("localhost", 3000);
            System.out.println("Connected to server");
            Thread readThread = new Thread(new ReadThread(this, s));
            readThread.start();
            Thread writeThread = new Thread(new WriteThread(this, s));
            writeThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
