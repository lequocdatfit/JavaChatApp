package Thread;

import views.ClientFrm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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
            reader = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        do {
            System.out.println("Reading");
        } while (true);
    }
}
