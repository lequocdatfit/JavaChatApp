package views;

import model.User;
import Thread.ClientThread;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerFrm extends JFrame{
    private JTextField txtPort;
    private JButton btnStartServer;
    private JTextArea txtServerLog;
    private JPanel rootPanel;
    private ServerSocket s;
    private int PORT = 3000;
    boolean isStarting = false;
    Thread serverThread;

    private ArrayList<ClientThread> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    private ArrayList<User> users = new ArrayList<>();

    public ServerFrm() {
        super();
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setTitle("Server Configuration");
        setLocationRelativeTo(null);
        btnStartServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isStarting) {
                    // stop server
                    stopServer();
                    isStarting = false;
                    txtPort.setEditable(true);
                    btnStartServer.setText("Start server");
                    return;
                }
                try {
                    txtServerLog.append("Server is starting...\n");
                    startServer();
                    isStarting = true;
                    btnStartServer.setText("Stop");
                    txtPort.setEditable(false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public void startServer() throws IOException {
        PORT = Integer.parseInt(txtPort.getText());
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    s = new ServerSocket(PORT);
                    while (true) {
                        txtServerLog.append("Server listening on port " + PORT + "\n");
                        Socket ss = s.accept();
                        txtServerLog.append("A user connected!\n");
                        ClientThread client = new ClientThread(ServerFrm.this, ss, clients);
                        pool.execute(client);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
    }

    public void stopServer() {
        try {
            serverThread.stop();
            s.close();
            txtServerLog.append("Server stopped.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ServerLogAppend(String text) {
        txtServerLog.append(text);
    }

    public void addUser(User s) {
        this.users.add(s);
    }
}
