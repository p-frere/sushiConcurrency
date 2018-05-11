package server;

import common.Order;
import common.Payload;
import common.TransactionType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



/**
 *  A thread that handles the sending and receiving of messages to a client
 */
public class ServerComms extends Thread {
    private Socket socket = null;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Server server;

    public ServerComms(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;

        //stores a reference to the thread and assigned user in the server class
        server.addUserThread(this);

        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("client has joined");

        //Sends init payload with data about postcode and menu
        sendMessage(new Payload(server.getUpdate(), TransactionType.updateInfo));
    }

    public void run(){
        Payload payload = null;
        try {
            while ((payload = (Payload) objectInputStream.readObject()) != null) {
                System.out.println("listening...");
                unpackPayload(payload);
            }
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            System.out.println("client has left");
        }
    }

    /**
     * Sends the payload to the client
     * @param payload
     */
    public void sendMessage(Payload payload) {
        System.out.println("sending payload ->");
        try {
            objectOutputStream.writeObject(payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unpacks the payload received and determiness
     * behaviour next passed on the transaction type
     * @param payload
     */
    public void unpackPayload(Payload payload){
        System.out.println("...stopped listening");
        System.out.println("received payload <-");
        switch (payload.getTransactionType()){
            case requestLogin:
                User user = server.login((User) payload.getObject());
                sendMessage(new Payload(user, TransactionType.replyLogin));
                break;
            case requestRegister:
                User newUser = server.register((User) payload.getObject());
                sendMessage(new Payload(newUser, TransactionType.requestRegister));
                break;
            case requestOrder:
                Order incomingOrder = (Order) payload.getObject();
                incomingOrder.setId(server.getID(this));
                server.addOrder(incomingOrder);
                break;
            default:
                System.out.println("unknown request");
                break;
        }

    }
}