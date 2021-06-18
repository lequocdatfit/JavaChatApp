package views;

import model.ServerDetail;
import model.ServerDetailRendered;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerListFrm extends JFrame {
    private JList<ServerDetail> listServer;
    private JButton btnAddConnect;
    private JPanel rootPanel;
    private JButton btnConnect;
    private JButton btnDelete;
    private JButton btnEdit;
    private LoginFrm login;
    private User currentUser;
    private List<ServerDetail> ls_servers = new ArrayList<>();
    DefaultListModel<ServerDetail> model;

    public void readFileConfig() {
        try {
            FileReader fr = new FileReader("config.txt");
            BufferedReader br = new BufferedReader(fr);
            String thisLine = null;
            while ((thisLine = br.readLine()) != null) {
                String[] host = thisLine.split(",");
                ServerDetail sv = new ServerDetail(host[0], Integer.parseInt(host[1]), "chat server");
                ls_servers.add(sv);
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerListFrm(Frame login, User user) {
        super();
        login = (LoginFrm) login;
        currentUser = user;
        setTitle("Chọn server");
        setContentPane(rootPanel);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        readFileConfig();

        model = new DefaultListModel<>();
        // set Jlist model
        listServer.setModel(model);
        listServer.setCellRenderer(new ServerDetailRendered());
        updateListServers();

        btnAddConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputServerFrm frm = new InputServerFrm(ServerListFrm.this, rootPaneCheckingEnabled);
                frm.setVisible(true);
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
                    ClientFrm frm = new ClientFrm(ServerListFrm.this, s, server, currentUser);
                    frm.setVisible(true);
                    setVisible(false);
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(rootPanel, "Kết nối thất bại!");
                    exception.printStackTrace();
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = listServer.getSelectedIndex();
                if(ls_servers.isEmpty()) {
                    JOptionPane.showMessageDialog(rootPanel, "Không có server nào để xóa!");
                } else if(index == -1) {
                    JOptionPane.showMessageDialog(rootPanel, "Hãy chọn một server!");
                } else {
                    int output = JOptionPane.showConfirmDialog(rootPanel, "Bạn có muốn xóa server này?",
                            "Cảnh báo", JOptionPane.YES_OPTION, JOptionPane.NO_OPTION);
                    if(output == JOptionPane.YES_OPTION) {
                        ls_servers.remove(index);
                        writeToFile();
                        updateListServers();
                    }
                }
            }
        });
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = listServer.getSelectedIndex();
                if(ls_servers.isEmpty()) {
                    JOptionPane.showMessageDialog(rootPanel, "Không có server nào để sửa!");
                } else if(index == -1) {
                    JOptionPane.showMessageDialog(rootPanel, "Hãy chọn một server!");
                } else {
                    EditServerFrm frm = new EditServerFrm(ServerListFrm.this, rootPaneCheckingEnabled);
                    frm.setEditData(ls_servers.get(index));
                    frm.setVisible(true);
                }
            }
        });
    }

    public void writeToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt", false));
            for (ServerDetail sv : ls_servers) {
                bw.write(sv.getHostName() + ',' + sv.getPort() + '\n');
            }
            bw.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void addConnection(ServerDetail sv) {
        ls_servers.add(sv);
        model.addElement(sv);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt", true));
            bw.write(sv.getHostName() + ',' + sv.getPort() + '\n');
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateListServers() {
        // add item to model
        model.removeAllElements();
        for (ServerDetail sv : ls_servers) {
            model.addElement(sv);
        }
    }
}
