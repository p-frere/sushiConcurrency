package client;

import common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientComms implements Runnable{
    Client client;
    public static Socket socket;
    private ObjectOutputStream objectOutputStream;

    public ClientComms(Client client){
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Listening...");
        ObjectInputStream objectInputStream = null;

        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            while(true) {
                Payload payload = (Payload) objectInputStream.readObject();
                unpackPayload(payload);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Starts the socket that will allow communication
     * between the server and the client
     */
    public void initSocket()  {
        try {
            socket = new Socket("localhost", 4444);
            System.out.println("init socket");
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Cannot connect to Server");
        }
    }

    /**
     * Submits text to the serer
     * @param payload a collection of classes
     */
    public void send(Payload payload) {
        try {
            objectOutputStream.writeObject(payload);
            objectOutputStream.reset();
            System.out.println("Sent message ->");
        } catch (IOException e) {
            System.out.println("error in print stream");
            e.printStackTrace();
        }
    }

    /**
     * Unpacks the payload received and determines
     * behaviour by reading on the transaction type
     * @param payload package received
     */
    private void unpackPayload(Payload payload){
        System.out.println("received payload <-");

        switch (payload.getTransactionType()){
            case updateInfo:
                Update update = (Update) payload.getObject();
                client.updateInfo(update.getDishes(), update.getPostcodes());
                break;
            case replyLogin:
                client.setUser((User) payload.getObject());
                break;
            case requestRegister:
                client.setUser((User) payload.getObject());
                break;
            case deliverOrder:
                Order incomingOrder =(Order) payload.getObject();
                for(Order order : client.getOrders(incomingOrder.getUser())){
                    if (order.getOrderID() != null) {
                        if (order.getOrderID().equals(incomingOrder.getOrderID())) {
                            order.setStatus(OrderStatus.COMPLETE);
                        }
                    }
                }
                break;
            default:
                System.out.println("WARNING: unknown request, no action taken");
                break;
        }
    }

}
