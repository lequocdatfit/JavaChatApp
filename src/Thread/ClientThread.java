package Thread;

import model.User;
import views.ServerFrm;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread implements Runnable{
    private Socket client;
    private User user;
    private BufferedReader in;
    private BufferedWriter out;
    private ServerFrm serverFrm;

    public ClientThread(Frame server, Socket client, ArrayList<ClientThread> clients) throws IOException {
        this.client = client;
        serverFrm = (ServerFrm) server;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            String request = "";
            do {
                 request = in.readLine();
                serverFrm.ServerLogAppend(request);

            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
