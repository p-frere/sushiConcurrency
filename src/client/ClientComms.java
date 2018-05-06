package client;

import common.Payload;
import common.TransactionType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientComms implements Runnable{

    @Override
    public void run() {
        System.out.println("Listening...");
        ObjectInputStream objectInputStream = null;

        try {
            //Socket socket = new Socket("127.0.0.1", 1342);
            objectInputStream = new ObjectInputStream(Client.socket.getInputStream());
            while(true) {
                Payload payload = (Payload) objectInputStream.readObject();
                System.out.println("recived");
                doSomething(payload);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void doSomething(Payload payload){

    }

}
