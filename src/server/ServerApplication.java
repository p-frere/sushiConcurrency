package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import common.*;

public class ServerApplication {


    int PORT = 1342;
    Socket socket = null;
    //keeps track of number of users
    static ArrayList<Comms> userlist = new ArrayList<Comms>();

    public static void main(String[] args){

        ServerApplication server = new ServerApplication();
        try {
            server.initSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //adds another user
    public static void addUser(Comms st) {
        userlist.add(st);
    }

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
}
