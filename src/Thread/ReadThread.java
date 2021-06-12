package Thread;

import model.Message;
import model.User;
import views.ClientFrm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ReadThread implements Runnable{
    private ObjectInputStream reader;
    private Socket socket;
    private ClientFrm client;
    public ReadThread(ClientFrm c, Socket s) {
        this.client = c;
        this.socket = s;
    }

    @Override
    public void run() {
        try {
            reader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        Message response = null;
        do {
            try {
                response = (Message) reader.readObject();
                if(response.getType().equals("FETCH_USERS")) {
                    System.out.println(response.getType());
                    client.updateListUsers((ArrayList<User>) response.getPayload());
                } else if(response.getType().equals("USER_CONN")){
                    System.out.println("USER_CONN");
                    client.setUserOnline((User) response.getPayload());
                }
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }

        } while (true);
    }
}
