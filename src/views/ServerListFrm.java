package views;

import model.ServerDetail;
import model.ServerDetailRendered;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class ServerListFrm extends JFrame {
    private JList<ServerDetail> listServer;
    private JButton btnAddConnect;
    private JPanel rootPanel;
    private JButton btnConnect;
    private LoginFrm login;

    public ServerListFrm(Frame login) {
        super();
        login = (LoginFrm) login;
        setTitle("Chọn server");
        setContentPane(rootPanel);
        setSize(500, 400);
        setLocationRelativeTo(null);

        createListServers();

        btnAddConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // connect
                ServerDetail server = listServer.getSelectedValue();
                Socket s = null;
                try {
                    s = new Socket(server.getHostName(), server.getPort());
                    JOptionPane.showMessageDialog(rootPanel, "Đã kết nối!");
                    ClientFrm frm = new ClientFrm(ServerListFrm.this, s);
                    frm.setVisible(true);
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(rootPanel, "Kết nối thất bại!");
                    exception.printStackTrace();
                }


            }
        });
    }

    private void createListServers() {
        // create List model
        DefaultListModel<ServerDetail> model = new DefaultListModel<>();
        // add item to model
        model.addElement(new ServerDetail("localhost", 3000, "chat server"));
        model.addElement(new ServerDetail("127.0.0.1", 2000, "chat server"));
        model.addElement(new ServerDetail("192.168.0.2", 8000, "chat server"));
        // set Jlist model
        listServer.setModel(model);
        listServer.setCellRenderer(new ServerDetailRendered());
    }
}
