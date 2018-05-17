package common;

import java.io.Serializable;
import java.util.*;

/**
 * /**
 * A User is a customer to the sushi ordering service,
 * The data stored includes their login credentials, an address and previous orders
 */
public class User extends Model implements Serializable {
    private String name;
    private String password;
    private String address;
    private Postcode postCode;
    private Integer threadID;   //The threadID stores which server thread a user is assigned to when they connect to the server
    private List<Order> orders;
    private Map<Dish, Number> basket;

    //constructor for initial creation
    public User(String userName, String password, String address, Postcode postCode)
    {
        this.name = userName;
        this.password = password;
        this.address = address;
        this.postCode = postCode;
        orders = new ArrayList<>();
        basket = new HashMap<>();
        threadID = null;
    }

    //constructor for recovery of the user from a file
    public User(String userName, String password)
    {
        this.name = userName;
        this.password = password;
    }

    //settos and gettos
    public String getUserName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Postcode getPostCode() {
        return postCode;
    }

    public String getAddress(){
        return address;
    }

    public String getName() {
        return name;
    }

    public Integer getThreadID() {
        return threadID;
    }

    public void setThreadID(Integer threadID) {
        this.threadID = threadID;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order, boolean createNew){
        orders.add(order);
        if (createNew) {
            order.setOrderID(new Random().nextInt(900000));
        }
    }

    public Map<Dish, Number> getBasket() {
        return basket;
    }

    public void setBasket(Map<Dish, Number> basket) {
        this.basket = basket;
    }

    public void updateBasket(Dish dish, Number quantity) {
        basket.put(dish, quantity);
    }


}

