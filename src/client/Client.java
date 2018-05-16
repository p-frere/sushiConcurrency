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
    private ClientComms comms;
    private User user;

    private List<Postcode> postcodes;
    private List<Dish> dishes;
    //private Map<Dish, Number> basket;
    //private List<Order> orders;
    private UpdateListener listener;
    boolean setUpComplete;

    public static Socket socket;
    ObjectOutputStream objectOutputStream;

    public Client(){
        setUpComplete = false;
        postcodes = new ArrayList<>();
        //basket = new HashMap<>();
        //orders = new ArrayList<>();
        dishes = new ArrayList<>();

        synchronized (this) {
            initSocket();
            try {
                wait(5000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }

        for(Postcode postcode : postcodes)
            System.out.println(postcode.getName());

        setUpComplete = true;

    }

    //-----------Comms---------------------

    public void initSocket()  {
        try {
            socket = new Socket("localhost", 4444);
            System.out.println("init socket");
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            new Thread(new ClientComms(this)).start();
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Cannot connect to Server");
        }
    }

    // sends submitted text to server
    public void send(Payload payload) {
        try {
            objectOutputStream.writeObject(payload);
            objectOutputStream  .reset();
            System.out.println("Sent message");
        } catch (IOException e) {
            System.out.println("error in print stream");
            e.printStackTrace();
        }
    }

    public void updateInfo(List<Dish> dishes, List<Postcode> postcodes) {
        synchronized (this) {
            this.dishes = dishes;
            this.postcodes = postcodes;
            if (setUpComplete)
                notifyUpdate();
            notify();
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

    //------------Basket------------------------

    @Override
    public Map<Dish, Number> getBasket(User user) {
        return user.getBasket();
    }

    @Override
    public Number getBasketCost(User user) {
        Integer total = 0;
        for(Dish dish : user.getBasket().keySet()){
            total+=(dish.getPrice() * user.getBasket().get(dish).intValue());
        }
        return total;
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        user.updateBasket(dish, quantity);
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        user.updateBasket(dish, quantity);
    }

    @Override
    public Order checkoutBasket(User user) {
        Order newOrder = new Order(user, user.getBasket());
        user.addOrder(newOrder);
        clearBasket(user);
        print(newOrder.getBasket());
        send(new Payload(newOrder, TransactionType.requestOrder));
        return newOrder;
    }

    @Override
    public void clearBasket(User user) {
        user.setBasket(new HashMap<>());
    }

    //----------------Orders-----------------------------------

    @Override
    public List<Order> getOrders(User user) {
        return user.getOrders();
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
        System.out.println("request to cancel");
        order.setStatus(OrderStatus.CANCELED);
        send(new Payload(order, TransactionType.requestCancel));
    }

    //----------updates-----------------------------


    @Override
    public void addUpdateListener(UpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void notifyUpdate() {
        listener.updated(new UpdateEvent());
    }

    //todo remove
    public void print(Map<Dish, Number> map){
        System.out.println();
        for(Map.Entry<Dish, Number> entry : map.entrySet()) {
            Dish dish = entry.getKey();
            Number value = entry.getValue();

            System.out.println(dish.getName() + " x" + value);

        }
        System.out.println();
    }
}
