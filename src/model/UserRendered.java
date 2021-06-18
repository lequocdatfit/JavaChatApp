package model;

import javax.swing.*;
import java.awt.*;

public class UserRendered extends JPanel implements ListCellRenderer<User> {
    private JLabel lbIcon = new JLabel();
    private JLabel lbUsername = new JLabel();
    private JLabel lbStatus = new JLabel();
    private JLabel lbNewMessage = new JLabel();
    private User currentUser;

    public UserRendered(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        //setBorder(BorderFactory.createMatteBorder(0, 0 , 1, 0, Color.gray));
        setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        JPanel panelText = new JPanel(new GridLayout(0, 1));
        panelText.add(lbUsername);
        panelText.add(lbStatus);
        panelText.add(lbNewMessage);
        add(lbIcon, BorderLayout.WEST);
        add(panelText, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/img/user-profile.png"))
                .getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
        lbIcon.setIcon(imageIcon);
        if(value.getId().equals(currentUser.getId())) {
            lbUsername.setText(value.getName() + " (You)");
        } else {
            lbUsername.setText(value.getName());
        }
        if(value.getHasNewMessage()) {
            lbNewMessage.setText("Bạn có tin nhắn mới!");
        } else {
            lbNewMessage.setText("");
        }
        lbStatus.setText(value.getConnected() ? "Online" : "Offline");
        lbStatus.setForeground(Color.GREEN);
        lbNewMessage.setForeground(Color.RED);
        lbNewMessage.setOpaque(true);
        lbUsername.setOpaque(true);
        lbStatus.setOpaque(true);
        lbIcon.setOpaque(true);
        // when select item
        if(isSelected) {
            lbUsername.setBackground(list.getSelectionBackground());
            lbStatus.setBackground(list.getSelectionBackground());
            lbIcon.setBackground(list.getSelectionBackground());
            lbNewMessage.setBackground(list.getSelectionBackground());
            setBackground(list.getSelectionBackground());
        } else {
            // when don't select
            lbUsername.setBackground(list.getBackground());
            lbStatus.setBackground(list.getBackground());
            lbIcon.setBackground(list.getBackground());
            lbNewMessage.setBackground(list.getBackground());
            setBackground(list.getBackground());
        }
        return this;
    }
}
