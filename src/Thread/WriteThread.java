package Thread;

import views.ClientFrm;

import java.awt.*;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteThread implements Runnable{
    private Socket socket;
    private ClientFrm client;
    private PrintWriter writer;

    public WriteThread(ClientFrm client, Socket s) {
        this.client = client;
        this.socket = s;
    }

    @Override
    public void run() {
        do {

        } while (true);
    }
}
