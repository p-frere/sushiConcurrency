package server;

import common.Payload;
import common.TransactionType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Assigns thread to each connected client
//sends messages out
//listens for input from socket
public class ServerComms extends Thread {
    Socket socket = null;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    Server server;

    public ServerComms(Socket socket, Server server) throws ClassNotFoundException {
        this.server = server;
        this.socket = socket;
        server.addUserThread(this);

        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("added new client");
    }

    public void run(){
        Payload payload = null;
        try {
            while ((payload = (Payload) objectInputStream.readObject()) != null) {
                System.out.println("in loop");
                doSomething(payload);
            }
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Sends a message
    public void sendMessage(Payload payload) {
        System.out.println("sending message");
        try {
            objectOutputStream.writeObject(payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doSomething(Payload payload){
        System.out.println("recived payload");
        switch (payload.getTransactionType()){
            case requetLogin:
                User user = server.login((User) payload.getObject());
                sendMessage(new Payload(user, TransactionType.replyLogin));
                break;
            case requestRegister:
                break;
            default:
                System.out.println("unknown request");
                break;
        }

    }
}