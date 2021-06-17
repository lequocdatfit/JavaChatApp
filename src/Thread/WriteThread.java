package Thread;

import model.Message;
import model.User;
import views.ClientFrm;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WriteThread implements Runnable{
    private Socket socket;
    private ClientFrm client;
    private ObjectOutputStream writer;
    private User user;
    private Message message;

    public WriteThread(ClientFrm client, Socket s, User user, Message msg, ObjectOutputStream out) {
        this.client = client;
        this.socket = s;
        this.user = user;
        this.message = msg;
        writer = out;
    }

    @Override
    public void run() {
        try {
            //writer = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            // tell server you're online
            writer.writeObject(message);
            if(message.getType().equals("PRIVATE_MESSAGE")) {
                System.out.println("sending private");
            }
            writer.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        /*switch (message.getType()) {
            case "SESSION":

                break;
            case "PRIVATE_MESSAGE":

            default:
                break;
        }*/
    }
}
