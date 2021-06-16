package views;

import DAO.DAO;
import Thread.WriteThread;
import Thread.ReadThread;
import model.Message;
import model.MessageStore;
import model.User;
import model.UserRendered;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
    private HTMLDocument doc;
    private ServerListFrm serverList;
    private DefaultListModel<User> usersListModel;

    private Socket socket;
    private User currentUser;
    private ArrayList<User> listUser;
    private ObjectOutputStream writer;

    public ObjectOutputStream getObjectOutputStream() {
        return this.writer;
    }

    public ClientFrm(Frame serverList, Socket s, User user) {
        super();
        setTitle("Chat app");
        setContentPane(rootPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.socket = s;
        this.serverList = (ServerListFrm) serverList;
        currentUser = user;
        try {
            writer = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        //MessageArea.setContentType("text/html");

        doc = (HTMLDocument) MessageArea.getStyledDocument();

        txtMessage.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        txtMessage.setMargin(new Insets(10, 10, 10, 10));


        scrollPanelMsg.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        usersListModel = new DefaultListModel<>();
        jListUsers.setModel(usersListModel);
        jListUsers.setCellRenderer(new UserRendered());


        jListUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println("state change!");
                MessageArea.setText("<br/>");
                // find message for selected user
                User selectedUser = ((User) jListUsers.getSelectedValue());
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
    }

    /*public void execute() {
        try {
            s = new Socket("localhost", 3000);
            System.out.println("Connected to server");
            Thread readThread = new Thread(new ReadThread(this, s));
            readThread.start();
            Thread writeThread = new Thread(new WriteThread(this, s, currentUser, ));
            writeThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void setDefaultUserSelection() {
        // default select user;
        jListUsers.setSelectedIndex(0);
    }

    public void updateListUsers(ArrayList<User> users) {
        listUser = users;

        for (User u : listUser) {
            usersListModel.addElement(u);
        }
    }

    public void setUserOnline(User u) {
        /*for (User user : listUser) {
            if(user.getId().equals(u.getId())) {
                user.setConnected(true);
            }
        }*/
        u.setConnected(true);
        listUser.add(u);
        usersListModel.addElement(u);
    }

    public void onPrivateMessage(Message msg) {
        MessageStore.saveMessage(msg.getFrom(), msg);
        if(((User) jListUsers.getSelectedValue()).getId().equals(msg.getFrom())) {
            //MessageArea.append((String) msg.getPayload() + "\n");
            User from = new DAO().getUserById(msg.getFrom());
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
            }
            catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
        }
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
