package views;

import model.Client;
import model.User;
import Thread.ClientThread;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerFrm extends JFrame{
    private JTextField txtPort;
    private JButton btnStartServer;
    private JTextArea txtServerLog;
    private JPanel rootPanel;
    private JScrollPane scrollPanel;
    private JTable tblClient;
    private DefaultTableModel clientModel;
    private ServerSocket s;
    private List<Client> listClient;
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
        setSize(700, 600);
        setTitle("Server Configuration");
        setLocationRelativeTo(null);

        listClient = new ArrayList<>();
        clientModel = (DefaultTableModel) tblClient.getModel();
        clientModel.setColumnIdentifiers(new Object[] {
                "STT", "IP address", "Port", "Username"
        });
        tblClient.setModel(clientModel);


        scrollPanel.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
        btnStartServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isStarting) {
                    // stop server
                    stopServer();
                    isStarting = false;
                    txtPort.setEditable(true);
                    btnStartServer.setText("Start server");
                    clearClientTable();
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

    public void clearClientTable() {
        clientModel.setRowCount(0);
    }

    public void startServer() throws IOException {
        PORT = Integer.parseInt(txtPort.getText());
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    s = new ServerSocket(PORT);
                    txtServerLog.append("Server listening on port " + PORT + "\n");
                    while (true) {
                        txtServerLog.append("Waiting for client...\n");
                        Socket ss = s.accept();
                        txtServerLog.append("A user connected!\n");
                        ClientThread client = new ClientThread(ServerFrm.this, ss, clients);
                        clients.add(client);
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

    public void updateClientTable() {
        clientModel.setRowCount(0);
        for (int i=0; i<listClient.size(); i++) {
            Client cl = listClient.get(i);
            clientModel.addRow(new Object[] {
                i + 1, cl.getIpAddress(), cl.getPort(), cl.getUsername()
            });
        }

    }

    public void addNewClient(Client client) {
        listClient.add(client);
    }

    public void ServerLogAppend(String text) {
        txtServerLog.append(text);
    }

    public void addUser(User s) {
        this.users.add(s);
    }
}
