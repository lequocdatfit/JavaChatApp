package views;

import model.ServerDetail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditServerFrm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtHostname;
    private JTextField txtPort;
    private ServerListFrm frm;
    private ServerDetail selectedServer;

    public EditServerFrm(Frame parent, boolean modal) {
        super(parent, modal);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();
        setLocationRelativeTo(parent);
        frm = (ServerListFrm) parent;

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String hostname = txtHostname.getText();
                Integer port = Integer.valueOf(txtPort.getText());
                selectedServer.setHostName(hostname);
                selectedServer.setPort(port);
                frm.writeToFile();
                frm.updateListServers();
                dispose();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void setEditData(ServerDetail sv) {
        selectedServer = sv;
        txtHostname.setText(sv.getHostName());
        txtPort.setText(sv.getPort().toString());
    }
}
