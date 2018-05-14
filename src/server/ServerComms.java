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
 *  Each thread listens to messages from a particular client and sends messages out to them
 */
public class ServerComms extends Thread {
    private Socket socket = null;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Server server;
    private Integer ID;

    //Constructor
    public ServerComms(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;

        //stores a reference to the thread so it can be addressed by the server
        ID = server.addUserThread(this);

        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("new client connected");

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
            //e.printStackTrace();
        } finally {
            System.out.println("client has disconnected");
            server.removeUserThread(this);
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
     * Unpacks the payload received and determines
     * behaviour by reading on the transaction type
     * @param payload
     */
    public void unpackPayload(Payload payload){
        System.out.println("...stopped listening");
        System.out.println("received payload <-");
        switch (payload.getTransactionType()){
            case initUser:
                User currentUser = server.getUSer(payload.getObject().getName());
                System.out.println(currentUser.getName());
                currentUser.setThreadID(ID);
                break;
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
                incomingOrder.setServerID(server.getID(this));
                server.addOrder(incomingOrder);
                break;
            case requestCancel:
                Order toCancel = (Order)payload.getObject();
                server.removeOrder(toCancel);
            default:
                System.out.println("unknown request");
                break;
        }

    }
}