package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingFrm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton lineBreakRadio;
    private JRadioButton sendMsgRadio;
    private ClientFrm clientFrm;

    public SettingFrm(Frame parent, boolean modal) {
        super(parent, modal);
        setContentPane(contentPane);
        setModal(true);
        pack();
        setLocationRelativeTo(parent);
        getRootPane().setDefaultButton(buttonOK);
        clientFrm = (ClientFrm) parent;

        if(clientFrm.isLineBreak()) {
            lineBreakRadio.setSelected(true);
        } else {
            lineBreakRadio.setSelected(false);
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(lineBreakRadio.isSelected()) {
                    clientFrm.setLineBreak(true);
                } else {
                    clientFrm.setLineBreak(false);
                }
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

}
