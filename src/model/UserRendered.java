package model;

import javax.swing.*;
import java.awt.*;

public class UserRendered extends JPanel implements ListCellRenderer<User> {
    private JLabel lbIcon = new JLabel();
    private JLabel lbUsername = new JLabel();
    private JLabel lbStatus = new JLabel();

    public UserRendered() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createMatteBorder(0, 0 , 1, 0, Color.gray));
        JPanel panelText = new JPanel(new GridLayout(0, 1));
        panelText.add(lbUsername);
        panelText.add(lbStatus);
        add(lbIcon, BorderLayout.WEST);
        add(panelText, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/img/user-profile.png"))
                .getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        lbIcon.setIcon(imageIcon);
        lbUsername.setText(value.getName());
        lbStatus.setText(value.getConnected() ? "Online" : "Offline");
        lbStatus.setForeground(Color.BLUE);
        lbUsername.setOpaque(true);
        lbStatus.setOpaque(true);
        lbIcon.setOpaque(true);
        // when select item
        if(isSelected) {
            lbUsername.setBackground(list.getSelectionBackground());
            lbStatus.setBackground(list.getSelectionBackground());
            lbIcon.setBackground(list.getSelectionBackground());
            setBackground(list.getSelectionBackground());
        } else {
            // when don't select
            lbUsername.setBackground(list.getBackground());
            lbStatus.setBackground(list.getBackground());
            lbIcon.setBackground(list.getBackground());
            setBackground(list.getBackground());
        }
        return this;
    }
}
