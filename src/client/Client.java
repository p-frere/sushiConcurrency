package client;

import common.*;
import javafx.geometry.Pos;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements ClientInterface {
    ClientWindow window;
    ClientComms comms;
    User user;

    List<Postcode> postcodes;
    List<Dish> dishes;
    Map<Dish, Number> basket;
    List<Order> orders;

    public static Socket socket;
    ObjectOutputStream objectOutputStream;

    public Client(){


        postcodes = new ArrayList<>();
        dishes = new ArrayList<>();
        basket = new HashMap<>();
        orders = new ArrayList<>();

        synchronized (this) {
            initSocket();
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(Postcode postcode : postcodes)
            System.out.println(postcode.getName());
        window = new ClientWindow(this);


    }

    //-----------Comms---------------------

    public void initSocket()  {
        try {
            socket = new Socket("localhost", 4444);
            System.out.println("init socket");
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            new Thread(new ClientComms(this)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // sends submitted text to server
    public void send(Payload payload) {
        try {
            objectOutputStream.writeObject(payload);
            System.out.println("Sent message");
        } catch (IOException e) {
            System.out.println("error in print stream");
            e.printStackTrace();
        }
    }

    //------------User------------------------

    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        User newUser = new User(username, password, address, postcode);
        send(new Payload(newUser, TransactionType.requestRegister));

        synchronized (this) {
            System.out.println("register waiting...");

            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("...register finished waiting");
        }
        return user;
    }

    @Override
    public User login(String username, String password) {
        User returningUser = new User(username, password);
        send(new Payload(returningUser, TransactionType.requestLogin));

        synchronized (this) {
            System.out.println("login waiting...");

            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("...login finished waiting");
        }

        return user;
    }

    public void setUser(User user){
        synchronized (this) {
            this.user = user;
            if (user == null) {
                System.out.println("user is null");
            }
            notify();
        }
        send(new Payload(user, TransactionType.initUser));
    }

    //---------Postcodes----------------------------
    public void updateInfo(List<Dish> dishes, List<Postcode> postcodes) {
        synchronized (this) {
            this.dishes = dishes;
            this.postcodes = postcodes;
            notify();
        }
    }

    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    //-------------Dishes--------------------------------
    @Override
    public List<Dish> getDishes() {
        return dishes;
    }

    @Override
    public String getDishDescription(Dish dish) {
        return dish.getDescription();
    }

    @Override
    public Number getDishPrice(Dish dish) {
        return dish.getPrice();
    }

    @Override
    public Map<Dish, Number> getBasket(User user) {
        return basket;
    }

    @Override
    public Number getBasketCost(User user) {
        Integer total = 0;
        for(Dish dish : basket.keySet()){
            total+=(dish.getPrice() * basket.get(dish).intValue());
        }
        return total;
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        basket.put(dish, quantity);
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        basket.put(dish, quantity);
    }

    @Override
    public Order checkoutBasket(User user) {
        Order newOrder = new Order(user, basket);
        orders.add(newOrder);
        send(new Payload(newOrder, TransactionType.requestOrder));
        return newOrder;
    }

    @Override
    public void clearBasket(User user) {
        basket.clear();
    }

    //----------------Orders-----------------------------------

    @Override
    public List<Order> getOrders(User user) {
        return orders;
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return (order.getStatus() == OrderStatus.COMPLETE);
    }

    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus().toString();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getOrderCost();
    }

    @Override
    public void cancelOrder(Order order) {
        send(new Payload(order, TransactionType.requestCancel));
        orders.remove(order);
    }

    //----------updates-----------------------------

    @Override
    public void addUpdateListener(UpdateListener listener) {

    }

    @Override
    public void notifyUpdate() {

    }
}
