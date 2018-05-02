package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerComms {
    int PORT = 1342;
    Socket socket = null;
    //keeps track of number of users
    static ArrayList<Comms> userlist = new ArrayList<Comms>();


    //server setup
    public void initSocket() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server Started");

        while (true) {
            //server listens for connections
            socket = serverSocket.accept();
            //accepts connection and creates new thread for user
            //start() calls the run() method on a new thread
            new Comms(socket).start();

        }
    }

    //adds another user
    public static void addUser(Comms st) {
        userlist.add(st);
    }

    //sends to all users
    public static void sendToAll(String text) {
        //for all threads, send the message back
        for(Comms st : userlist){
            try {
                st.sendMessage(text);
            } catch (IOException e) {
                System.out.println("can't relay info to all threads");
                e.printStackTrace();
            }
        }

    }

    //sends to specific user
    public static void sendTo(Comms user, String text) {
        //for all threads, send the message back
        try {
            user.sendMessage(text);
        } catch (IOException e) {
            System.out.println("can't relay info to thread");
            e.printStackTrace();
        }
    }
}