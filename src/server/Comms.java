package server;

import common.Payload;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//Listends for new incomming connections
//from clients

public class Comms extends Thread{ //listner
    Server server;

    public Comms(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Server.PORT);
            System.out.println("Listening for new clients...");

            while (true) {
                //server listens for connections
                Socket socket = null;
                socket = serverSocket.accept();
                //accepts connection and creates new thread for user
                //start() calls the run() method on a new thread
                new ServerComms(socket, server).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}

