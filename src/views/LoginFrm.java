package views;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrm extends JFrame {
    private JPanel contentPanel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;

    public LoginFrm() {
        super();
        setTitle("Đăng nhập");
        setContentPane(contentPanel);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = txtUsername.getText();
                String password = txtPassword.getText();
                if(name.equals("user") && password.equals("12345")) {
                    JOptionPane.showMessageDialog(contentPanel, "Đăng nhập thành công!");
                    ServerListFrm frm = new ServerListFrm(LoginFrm.this);
                    frm.setVisible(true);
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(contentPanel, "Tài khoản hoặc mật khẩu không đúng!");
                }
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
