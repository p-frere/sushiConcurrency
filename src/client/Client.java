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

/**
 * The Client handle the client program's backend
 * When logged in, the application should show all available dishes.
 * Customers can add/remove dishes to/from a shopping basket,
 * view the current total price and place their order.
 * They can also see the status of current and previous orders.
 */
public class Client implements ClientInterface {
    private ClientComms comms;
    private User user;

    private List<Postcode> postcodes;
    private List<Dish> dishes;
    private List<UpdateListener> listeners;
    private boolean setUpComplete;


    //constructor
    public Client(){
        setUpComplete = false;
        listeners = new ArrayList<>();
        postcodes = new ArrayList<>();
        dishes = new ArrayList<>();
        comms = new ClientComms(this);
        new Thread(comms).start();

        synchronized (this) {
            comms.initSocket();
            //creates a time out
            try {
                wait(5000);
            } catch (InterruptedException e) {
            }
        }

        for(Postcode postcode : postcodes)
            System.out.println(postcode.getName());

        setUpComplete = true;
    }

    //-----------Comms---------------------

    /**
     * Updates the dishes and the post codes
     * to newly delivered ones
     * @param dishes list of dishes
     * @param postcodes list of postcodes
     */
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
        comms.send(new Payload(newUser, TransactionType.requestRegister));

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
        comms.send(new Payload(returningUser, TransactionType.requestLogin));

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

    /**
     * Sets the client window for a delivered user
     * @param user
     */
    public void setUser(User user){
        synchronized (this) {
            this.user = user;
            if (user == null) {
                System.out.println("user is null");
            }
            notify();
        }
        comms.send(new Payload(user, TransactionType.initUser));
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

    //aka new order
    @Override
    public Order checkoutBasket(User user) {
        Order newOrder = new Order(user, user.getBasket());
        user.addOrder(newOrder, true);
        clearBasket(user);
        comms.send(new Payload(newOrder, TransactionType.requestOrder));
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
        System.out.println(order.getOrderID());
        comms.send(new Payload(order, TransactionType.requestCancel));
    }

    //----------updates-----------------------------


    @Override
    public void addUpdateListener(UpdateListener listener) {
        listeners.add(listener);
    }

    @Override
    public void notifyUpdate() {
        for(UpdateListener listener : listeners) {
            listener.updated(new UpdateEvent());
        }
    }
}
