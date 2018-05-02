package client;

import common.Payload;
import common.TransactionType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientComms {
    Socket socket;
    Client client;

    public ClientComms(Client client){
        this.client = client;
    }

    // Client Setup
    public void initSocket() throws IOException {
        try {
            socket = new Socket("127.0.0.1", 1342);
        } catch (IOException e) {
            System.out.println("socket delceration error");
            e.printStackTrace();
        }

        // accepting something from the server
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        try {
            Payload payload = (Payload)ois.readObject();
            switch (payload.getTransactionType()){
                case replyLogin:
                    break;
                case replyOrder:
                    break;
                case replyStock:
                    break;
                default:
                    System.out.println("Unknown package sent");
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    // sends submitted text to server
    public void send(Payload payload) {
        //todo fix to object serilization
        // passes to the server with the username
        PrintStream ps;
        try {
            ps = new PrintStream(socket.getOutputStream());
            ps.println(payload);

        } catch (IOException e) {
            System.out.println("error in print stream");
            e.printStackTrace();
        }
    }
}
