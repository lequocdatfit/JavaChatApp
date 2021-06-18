package views;

import DAO.DAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpFrm extends JFrame{
    private JTextField txtNameLogin;
    private JTextField txtNickname;
    private JPanel rootPanel;
    private JButton btnSignUp;
    private JButton thoátButton;
    private JPasswordField txtCheck;
    private JPasswordField txtPassword;
    private LoginFrm loginFrm;

    public SignUpFrm(Frame login) {
        super();
        this.setTitle("Đăng ký");
        this.setContentPane(rootPanel);
        setSize(400, 300);
        this.setLocationRelativeTo(null);
        loginFrm = (LoginFrm) login;
        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameLogin = txtNameLogin.getText();
                String nickName = txtNickname.getText();
                String password = txtPassword.getText();
                String check = txtCheck.getText();
                if(nameLogin.length() < 8 || nameLogin.length() > 30 || nameLogin.contains(" ")) {
                    JOptionPane.showMessageDialog(rootPanel, "Tên đăng nhập từ 8 đến 30 ký tự, không chứa khoảng trắng");
                    return;
                }
                if(nickName.length() < 2 || nickName.length() > 30) {
                    JOptionPane.showMessageDialog(rootPanel, "NickName có độ dài từ 2 đến 30 ký tự");
                    return;
                }
                if(password.length() < 8 || password.contains(" ") || password.length() > 30) {
                    JOptionPane.showMessageDialog(rootPanel, "Mật khẩu không chứa khoảng trằng, độ dài từ 8 đến 30");
                } else if(!password.equals(check)){
                    JOptionPane.showMessageDialog(rootPanel, "Mật khẩu không khớp!");
                } else {
                    User user = new User(nameLogin, nickName, password);
                    if(addNewUser(user)) {
                        JOptionPane.showMessageDialog(rootPanel, "Đăng ký thành công!");
                        dispose();
                        login.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(rootPanel, "Đăng ký thất bại!");
                    }
                }
            }
        });
        thoátButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                login.setVisible(true);
            }
        });
    }
    public boolean addNewUser(User user) {
        if(new DAO().addNewUser(user)) {
            return true;
        }
        return false;
    }
}
