package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens for new incoming connections from clients
 */
public class Comms extends Thread{
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
            //server listens for connections
            while (true) {
                //accepts connection and creates new thread for user
                Socket socket = null;
                socket = serverSocket.accept();
                new ServerComms(socket, server).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

