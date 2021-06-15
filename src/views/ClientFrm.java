package views;

import Thread.WriteThread;
import Thread.ReadThread;
import model.Message;
import model.MessageStore;
import model.User;
import model.UserRendered;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private HTMLDocument doc;
    private ServerListFrm serverList;
    private DefaultListModel<User> usersListModel;

    private Socket s;
    private User currentUser;
    private ArrayList<User> listUser;
    private ObjectOutputStream writer;

    public ClientFrm(Frame serverList, Socket s, User user) {
        super();
        setTitle("Chat app");
        setContentPane(rootPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.serverList = (ServerListFrm) serverList;
        currentUser = user;
        try {
            writer = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        MessageArea.setContentType("text/html");

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
        // default select user;
        jListUsers.setSelectedIndex(0);

        jListUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println("state change!");
                MessageArea.setText("<br/>");
                // find message for selected user
                String selectedUserId = ((User) jListUsers.getSelectedValue()).getId();
                Message[] listMessages = MessageStore.findMessageForUser(selectedUserId);
                if(listMessages != null) {
                    for (Message msg: listMessages) {
                        //MessageArea.append((String) msg.getPayload() + "\n");
                        /*try {
                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), "<strong>"+ (String) msg.getPayload() + "</strong>");
                        } catch (BadLocationException | IOException badLocationException) {
                            badLocationException.printStackTrace();
                        }*/
                        try {
                            if(msg.getPayload().equals("(y)")) {
                                String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();
                                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                        "<img src='"+ url + "'/><br/>");
                            } else if(msg.getPayload().equals("^_^")) {
                                String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();
                                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                        "<img src='"+ url + "'/><br/>");
                            } else if(msg.getPayload().equals(">:0")) {
                                String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();
                                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                        "<img src='"+ url + "'/><br/>");
                            } else if(msg.getPayload().equals(":(")) {
                                String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();
                                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                        "<img src='"+ url + "'/><br/>");
                            } else if(msg.getPayload().equals(":O")) {
                                String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();
                                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                        "<img src='"+ url + "'/><br/>");
                            }
                            else {
                                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), "<strong>"+ (String) msg.getPayload() + "</strong><br/>");
                            }
                        }
                        catch (BadLocationException | IOException badLocationException) {
                            badLocationException.printStackTrace();
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
                                "<div style='display: inline-block; margin:0 0 26px; text-align: right;\n" +
                                        "  padding: 0 0 0 220px;\n" +
                                        "  vertical-align: top;\n" +
                                        "  width: 92%;'><div style='width: 57%;'>" +
                                        "<strong style='background: #05728F none repeat scroll 0 0;\n" +
                                        "  border-radius: 3px;\n" +
                                        "  color: #ffffff;\n" +
                                        "  font-size: 14px;\n" +
                                        "  margin: 0 0 20 0;\n" +
                                        "  padding: 5px 10px 5px 12px;\n" +
                                        "  width: 100%;'>"+ content + "</strong>" +
                                        "</div></div><br/>");
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
                            "<img src='"+ url + "'/><br/>");
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
                            "<img src='"+ url + "'/><br/>");
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
                            "<img src='"+ url + "'/><br/>");
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
                            "<img src='"+ url + "'/><br/>");
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
                            "<img src='"+ url + "'/><br/>");
                } catch (BadLocationException | IOException badLocationException) {
                    badLocationException.printStackTrace();
                }
                MessageStore.saveMessage(to, privateMessage);
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

            try {
                if(msg.getPayload().equals("(y)")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<img src='"+ url + "'/><br/>");
                } else if(msg.getPayload().equals("^_^")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<img src='"+ url + "'/><br/>");
                } else if(msg.getPayload().equals(">:0")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<img src='"+ url + "'/><br/>");
                } else if(msg.getPayload().equals(":(")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<img src='"+ url + "'/><br/>");
                } else if(msg.getPayload().equals(":O")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<img src='"+ url + "'/><br/>");
                }
                else {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div style='display: inline-block; margin:0 0 26px;\n" +
                            "  padding: 0 0 0 10px;\n" +
                            "  vertical-align: top;\n" +
                            "  width: 92%;'><div style='width: 57%; float: left;'>" +
                            "<strong style='background: #ebebeb none repeat scroll 0 0;\n" +
                                    "  border-radius: 3px;\n" +
                                    "  color: #646464;\n" +
                                    "  font-size: 14px;\n" +
                                    "  margin: 0 0 20 0;\n" +
                                    "  padding: 5px 10px 5px 12px;\n" +
                                    "  width: 100%;'>"+ (String) msg.getPayload() + "</strong>" +
                            "</div></div><br/>");
                }
            }
            catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
        }
    }
}
