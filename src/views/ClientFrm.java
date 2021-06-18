package views;

import DAO.DAO;
import Thread.WriteThread;
import Thread.ReadThread;
import model.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientFrm extends JFrame {
    private JPanel rootPanel;
    private JTextArea txtMessage;
    private JButton btnSend;
    private JList jListUsers;
    private JScrollPane scrollPanelMsg;
    private JButton btnLike;
    private JButton btnSad;
    private JButton btnSmile;
    private JButton btnHappy;
    private JButton btnShock;
    private JTextPane MessageArea;
    private JButton fileButton;
    private JButton btnSetting;
    private JPanel topPanel;
    private JPanel inputPanel;
    private JLabel txtServerDetail;
    private HTMLDocument doc;
    private ServerListFrm serverList;
    private DefaultListModel<User> usersListModel;
    private boolean lineBreak = false;
    private JScrollBar sb = scrollPanelMsg.getVerticalScrollBar();

    private Socket socket;
    private User currentUser;
    private ArrayList<User> listUser;
    private ObjectOutputStream writer;

    public boolean isLineBreak() {
        return lineBreak;
    }

    public void setLineBreak(boolean lineBreak) {
        this.lineBreak = lineBreak;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return this.writer;
    }

    public ClientFrm(Frame serverList, Socket s, ServerDetail svdel, User user) {
        super();
        setTitle("Bạn đã đăng nhập với tên: " + user.getName());
        setContentPane(rootPanel);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.socket = s;
        this.serverList = (ServerListFrm) serverList;
        currentUser = user;
        txtServerDetail.setText("Hostname: " + svdel.getHostName() + "\nPort: " + svdel.getPort());
        btnSend.setPreferredSize(new Dimension(50, 40));
        txtMessage.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        txtMessage.setMargin(new Insets(10, 10, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 3));

        topPanel.setLayout(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        JPanel userPanel = new JPanel(new GridLayout(0, 1));
        JLabel lbName = new JLabel();
        JLabel status = new JLabel();
        JLabel icon = new JLabel();
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/img/user-profile.png"))
                .getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        icon.setIcon(imageIcon);
        status.setForeground(Color.GREEN);
        lbName.setOpaque(true);
        status.setOpaque(true);
        icon.setOpaque(true);
        userPanel.add(lbName);
        userPanel.add(status);
        topPanel.add(userPanel, BorderLayout.CENTER);
        topPanel.add(icon, BorderLayout.WEST);
        /*lbName.setBackground(Color.white);
        status.setBackground(Color.white);
        icon.setBackground(Color.white);
        topPanel.setBackground(Color.white);*/

        try {
            writer = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Message disConnect = new Message("USER_DISCONNECT", ClientFrm.this.currentUser);
                Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, disConnect, writer));
                privateThread.start();
                System.out.println("Close");
            }
        });


        doc = (HTMLDocument) MessageArea.getStyledDocument();


        usersListModel = new DefaultListModel<>();
        jListUsers.setModel(usersListModel);
        jListUsers.setCellRenderer(new UserRendered(currentUser));


        jListUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println("state change!");

                MessageArea.setText("<br/>");
                // find message for selected user
                User selectedUser = ((User) jListUsers.getSelectedValue());

                if(selectedUser != null) {
                    lbName.setText(selectedUser.getName());
                    status.setText("Online");

                    if(selectedUser.getHasNewMessage() == true) {
                        selectedUser.setHasNewMessage(false);
                        updateUserList();
                        return;
                    }

                    String selectedUserId = selectedUser.getId();
                    Message[] listMessages = MessageStore.findMessageForUser(selectedUserId);
                    if(listMessages != null) {
                        for (Message msg: listMessages) {
                            //MessageArea.append((String) msg.getPayload() + "\n");
                        /*try {
                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), "<strong>"+ (String) msg.getPayload() + "</strong>");
                        } catch (BadLocationException | IOException badLocationException) {
                            badLocationException.printStackTrace();
                        }*/
                            if(selectedUserId.equals(msg.getFrom())) {
                                try {
                                    if(msg.getPayload().equals("(y)")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    } else if(msg.getPayload().equals("^_^")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    } else if(msg.getPayload().equals(">:0")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    } else if(msg.getPayload().equals(":(")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    } else if(msg.getPayload().equals(":O")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    }
                                    else {
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div style='background-color: #ebebeb; margin: 0 0 10px 0;'><pre style='color: #000;'>"
                                                        + "<span style='color: red;'>" + selectedUser.getName() + ": </span>" + (String) msg.getPayload() + "</pre></div><br/>");
                                    }
                                }
                                catch (BadLocationException | IOException badLocationException) {
                                    badLocationException.printStackTrace();
                                }
                            } else {
                                try {
                                    if(msg.getPayload().equals("(y)")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    } else if(msg.getPayload().equals("^_^")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    } else if(msg.getPayload().equals(">:0")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    } else if(msg.getPayload().equals(":(")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    } else if(msg.getPayload().equals(":O")) {
                                        String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                                    }
                                    else {
                                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                                "<div style='background-color: #05728F; margin: 0 0 10px 0;'><pre style='color: #fff'>"
                                                        + "<span style='color: yellow;'>you: </span>" + (String) msg.getPayload() + "</pre></div><br/>");
                                    }
                                }
                                catch (BadLocationException | IOException badLocationException) {
                                    badLocationException.printStackTrace();
                                }
                            }
                        }
                        validate();
                        sb.setValue( sb.getMaximum() );
                    }
                }
            }
        });

        Thread readThread = new Thread(new ReadThread(this, s));
        readThread.start();
        Message sessionEvent = new Message("SESSION", currentUser);
        Thread writeThread = new Thread(new WriteThread(this, s, currentUser, sessionEvent, writer));
        writeThread.start();
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = txtMessage.getText();
                if(!content.equals("")) {
                    String to = ((User) jListUsers.getSelectedValue()).getId();
                    Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                    Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, privateMessage, writer));
                    privateThread.start();
                    txtMessage.setText("");

                    try {
                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                "<div style='background-color: #05728F; margin: 0 0 10px 0;'><pre style='color: #fff'>"
                                + "<span style='color: yellow;'>you: </span>" + content + "</pre></div><br/>");
                    } catch (BadLocationException | IOException badLocationException) {
                        badLocationException.printStackTrace();
                    }

                    MessageStore.saveMessage(to, privateMessage);
                    //validate();
                    //sb.setValue( sb.getMaximum() );
                    MessageArea.setCaretPosition(MessageArea.getDocument().getLength());

                }
            }
        });


        btnLike.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = "(y)";
                String to = ((User) jListUsers.getSelectedValue()).getId();
                Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, privateMessage, writer));
                privateThread.start();
                txtMessage.setText("");

                String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();


                try {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } catch (BadLocationException | IOException badLocationException) {
                    badLocationException.printStackTrace();
                }
                MessageStore.saveMessage(to, privateMessage);
                validate();
                sb.setValue( sb.getMaximum() );
            }
        });
        btnSmile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = "^_^";
                String to = ((User) jListUsers.getSelectedValue()).getId();
                Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, privateMessage, writer));
                privateThread.start();
                txtMessage.setText("");

                String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();

                try {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } catch (BadLocationException | IOException badLocationException) {
                    badLocationException.printStackTrace();
                }
                MessageStore.saveMessage(to, privateMessage);
                validate();
                sb.setValue( sb.getMaximum() );
            }
        });
        btnHappy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = ">:0";
                String to = ((User) jListUsers.getSelectedValue()).getId();
                Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, privateMessage, writer));
                privateThread.start();
                txtMessage.setText("");

                String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();

                try {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } catch (BadLocationException | IOException badLocationException) {
                    badLocationException.printStackTrace();
                }

                MessageStore.saveMessage(to, privateMessage);
                validate();
                sb.setValue( sb.getMaximum() );
            }
        });
        btnSad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = ":(";
                String to = ((User) jListUsers.getSelectedValue()).getId();
                Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, privateMessage, writer));
                privateThread.start();
                txtMessage.setText("");

                String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();

                try {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } catch (BadLocationException | IOException badLocationException) {
                    badLocationException.printStackTrace();
                }
                MessageStore.saveMessage(to, privateMessage);
                validate();
                sb.setValue( sb.getMaximum() );
            }
        });
        btnShock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = ":O";
                String to = ((User) jListUsers.getSelectedValue()).getId();
                Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, privateMessage, writer));
                privateThread.start();
                txtMessage.setText("");

                String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();

                try {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: #000;'>you: </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } catch (BadLocationException | IOException badLocationException) {
                    badLocationException.printStackTrace();
                }
                MessageStore.saveMessage(to, privateMessage);
                validate();
                sb.setValue( sb.getMaximum() );
            }
        });
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User selectedUser =  ((User) jListUsers.getSelectedValue());
                SendFileFrm frm = new SendFileFrm(ClientFrm.this, socket, writer,
                        selectedUser, currentUser, rootPaneCheckingEnabled);
                frm.setVisible(true);
            }
        });
        btnSetting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingFrm frm = new SettingFrm(ClientFrm.this, rootPaneCheckingEnabled);
                frm.setVisible(true);
            }
        });

        txtMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !lineBreak) {
                    e.consume();
                    String content = txtMessage.getText();
                    if(!content.equals("")) {
                        String to = ((User) jListUsers.getSelectedValue()).getId();
                        Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                        Thread privateThread = new Thread(new WriteThread(ClientFrm.this, s, currentUser, privateMessage, writer));
                        privateThread.start();
                        txtMessage.setText("");

                        try {
                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                    "<div style='background-color: #05728F; margin: 0 0 10px 0;'><pre style='color: #fff'>"
                                            + "<span style='color: yellow;'>you: </span>" + content + "</pre></div><br/>");
                        } catch (BadLocationException | IOException badLocationException) {
                            badLocationException.printStackTrace();
                        }

                        MessageStore.saveMessage(to, privateMessage);
                        validate();
                        sb.setValue( sb.getMaximum() );
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }


    public void setDefaultUserSelection() {
        // default select user;
        jListUsers.setSelectedIndex(0);
    }

    public void updateListUsers(ArrayList<User> users) {
        listUser = users;
        //// put current user on the top
        for (int i=0; i<listUser.size(); i++) {
            User us = listUser.get(i);
            if(us.getId().equals(currentUser.getId())) {
                listUser.set(i, listUser.get(0));
                listUser.set(0, us);
            }
        }

        for (User u : listUser) {
            usersListModel.addElement(u);
        }
    }

    public void setUserOnline(User u) {
        u.setConnected(true);
        listUser.add(u);
        usersListModel.addElement(u);
    }

    public void setUserOffLine(User u) {
        u.setConnected(false);
        Iterator<User> itr = listUser.iterator();
        while (itr.hasNext()) {
            User user = itr.next();
            if(user.getId().equals(u.getId())) {
                itr.remove();
            }
        }
        User selectedUser = (User) jListUsers.getSelectedValue();
        if(selectedUser.getId().equals(u.getId())) {
            JOptionPane.showMessageDialog(rootPanel, selectedUser.getName() + " đã thoát!");
            usersListModel.remove(jListUsers.getSelectedIndex());
            jListUsers.setSelectedIndex(0);
        } else {
            usersListModel.removeElement(u);
        }
    }

    public void onPrivateMessage(Message msg) {
        MessageStore.saveMessage(msg.getFrom(), msg);
        User selectedUser = (User) jListUsers.getSelectedValue();
        User from = new DAO().getUserById(msg.getFrom());
        if(selectedUser.getId().equals(msg.getFrom())) {
            //MessageArea.append((String) msg.getPayload() + "\n");
            try {
                if(msg.getPayload().equals("(y)")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } else if(msg.getPayload().equals("^_^")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } else if(msg.getPayload().equals(">:0")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } else if(msg.getPayload().equals(":(")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } else if(msg.getPayload().equals(":O")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                }
                else {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div style='background-color: #ebebeb; margin: 0 0 10px 0;'><pre style='color: #000;'>"
                               + "<span style='color: red;'>" + from.getName() + ": </span>" + (String) msg.getPayload() + "</pre></div><br/>");
                }
                //validate();
                //sb.setValue( sb.getMaximum() );
                MessageArea.setCaretPosition(MessageArea.getDocument().getLength());
            }
            catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
        } else {

            for (User u : listUser) {
                if(u.getId().equals(from.getId())) {
                    u.setHasNewMessage(true);
                }
            }
            updateUserList();
        }
    }

    public void updateUserList() {
        int selectedIndex = jListUsers.getSelectedIndex();
        usersListModel = new DefaultListModel<>();
        jListUsers.setModel(usersListModel);
        jListUsers.setCellRenderer(new UserRendered(currentUser));

        usersListModel.removeAllElements();
        //// put current user on the top
        for (int i=0; i<listUser.size(); i++) {
            User us = listUser.get(i);
            if(us.getId().equals(currentUser.getId())) {
                listUser.set(i, listUser.get(0));
                listUser.set(0, us);
            }
        }

        for (User u : listUser) {
            usersListModel.addElement(u);
        }
        jListUsers.setSelectedIndex(selectedIndex);
    }


    public void onPrivateFileMessage(ObjectInputStream in, User from) {
        try {
            int fileNameLength = in.readInt();
            System.out.println(fileNameLength);
            if(fileNameLength > 0) {
                byte[] fileNameBytes = new byte[fileNameLength];
                in.readFully(fileNameBytes, 0, fileNameBytes.length);
                String fileName = new String(fileNameBytes);
                System.out.println(fileName);
                int fileContentLength = in.readInt();

                if (fileContentLength > 0) {
                    byte[] fileContentBytes = new byte[fileContentLength];
                    in.readFully(fileContentBytes, 0, fileContentLength);

                    int output = JOptionPane.showConfirmDialog(rootPanel, "Bạn nhận được một file từ: " + from.getName() + "\n" +
                            "Tên file: " + fileName +"\n" +
                            "Bạn có muốn lưu lại không?" , "Có file gửi đến", JOptionPane.YES_OPTION, JOptionPane.NO_OPTION);
                    if(output == JOptionPane.YES_OPTION) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Chọn nơi bạn cần lưu");
                        fileChooser.setSelectedFile(new File(fileName));
                        int userSelection = fileChooser.showSaveDialog(rootPanel);
                        if(userSelection == JFileChooser.APPROVE_OPTION) {
                            File fileToDownLoad = fileChooser.getSelectedFile();
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(fileToDownLoad);
                                fileOutputStream.write(fileContentBytes);
                                fileOutputStream.close();
                                JOptionPane.showMessageDialog(rootPanel, "Lưu thành công!");
                            } catch (IOException err) {
                                err.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch(IOException err) {
            err.printStackTrace();
        }

    }


}
