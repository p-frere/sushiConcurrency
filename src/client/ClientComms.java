package client;

import common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientComms implements Runnable{
    Client client;

    public ClientComms(Client client){
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Listening...");
        ObjectInputStream objectInputStream = null;

        try {
            objectInputStream = new ObjectInputStream(Client.socket.getInputStream());
            while(true) {
                Payload payload = (Payload) objectInputStream.readObject();
                System.out.println("recived");
                doSomething(payload);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    public void doSomething(Payload payload){
        System.out.println("received payload");

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
                    if(order.getOrderID() == incomingOrder.getOrderID()){
                        order.setStatus(OrderStatus.COMPLETE);
                    }
                }
                break;
            default:
                System.out.println("unknown request");
                break;
        }
    }

}
