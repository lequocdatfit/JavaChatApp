package views;

import model.Message;
import model.User;
import Thread.WriteThread;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SendFileFrm extends JDialog {
    private JPanel contentPane;
    private JButton btnSendFile;
    private JButton btnChooseFile;
    private JLabel jlFileName;
    private JButton buttonOK;
    private JButton buttonCancel;
    private ClientFrm clientFrm;

    private File[] fileToSend = new File[1];

    public SendFileFrm(Frame chatClient, Socket s, ObjectOutputStream writer, User selectedUser, User currentUser, boolean modal) {
        super(chatClient, modal);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.setSize(450, 450);
        this.setLocationRelativeTo(chatClient);
        clientFrm = (ClientFrm) chatClient;


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
        btnSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileToSend[0] == null) {
                    JOptionPane.showMessageDialog(contentPane, "Hãy chọn file trước khi gửi!");
                } else {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());

                        //DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());

                        String fileName = fileToSend[0].getName();
                        byte[] fileNameBytes = fileName.getBytes();

                        byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(fileContentBytes);

                        Message fileMessage = new Message("PRIVATE_FILE_MESSAGE", "file", currentUser.getId(), selectedUser.getId());
                        Thread privateThread = new Thread(new WriteThread(clientFrm, s, currentUser, fileMessage, ((ClientFrm) chatClient).getObjectOutputStream()));
                        privateThread.start();


                        Thread sendFile = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    writer.writeInt(fileNameBytes.length);
                                    writer.flush();
                                    writer.write(fileNameBytes);
                                    writer.flush();
                                    writer.writeInt(fileContentBytes.length);
                                    writer.flush();
                                    writer.write(fileContentBytes);
                                    writer.flush();
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        });
                        sendFile.start();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                }
            }
        });
        btnChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send");
                if(jFileChooser.showOpenDialog(clientFrm) == JFileChooser.APPROVE_OPTION) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("File bạn đã chọn : " + fileToSend[0].getName());
                }
            }
        });
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
