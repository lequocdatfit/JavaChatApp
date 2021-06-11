package model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class ServerDetailRendered extends JPanel implements ListCellRenderer<ServerDetail> {
    private JLabel lbIcon = new JLabel();
    private JLabel lbHostName = new JLabel();
    private JLabel lbPort = new JLabel();

    public ServerDetailRendered() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createMatteBorder(0, 0 , 1, 0, Color.gray));
        JPanel panelText = new JPanel(new GridLayout(0, 1));
        panelText.add(lbHostName);
        panelText.add(lbPort);
        add(lbIcon, BorderLayout.WEST);
        add(panelText, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ServerDetail> list, ServerDetail value, int index, boolean isSelected, boolean cellHasFocus) {
        lbIcon.setText("icon");
        lbHostName.setText(value.getHostName());
        lbPort.setText("Port: " + String.valueOf(value.getPort()));
        lbPort.setForeground(Color.BLUE);
        lbHostName.setOpaque(true);
        lbPort.setOpaque(true);
        lbIcon.setOpaque(true);
        // when select item
        if(isSelected) {
            lbHostName.setBackground(list.getSelectionBackground());
            lbPort.setBackground(list.getSelectionBackground());
            lbIcon.setBackground(list.getSelectionBackground());
            setBackground(list.getSelectionBackground());
        } else {
            // when don't select
            lbHostName.setBackground(list.getBackground());
            lbPort.setBackground(list.getBackground());
            lbIcon.setBackground(list.getBackground());
            setBackground(list.getBackground());
        }

        return this;
    }
}
