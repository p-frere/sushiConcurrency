package client;

import common.*;

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

    public Client(){
        window = new ClientWindow(this);
        comms = new ClientComms(this);

        postcodes = new ArrayList<>();
        dishes = new ArrayList<>();
        basket = new HashMap<>();
        orders = new ArrayList<>();
    }

    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        User newUser = new User(username, password, address, postcode);
        comms.send(new Payload(newUser, TransactionType.requestRegister));

        //this then needs to lock until notified
        //the server will reply
        //reply will be handled and user updated
        //then return current user

        return user;
    }

    @Override
    public User login(String username, String password) {
        User returningUser = new User(username, password);
        comms.send(new Payload(returningUser, TransactionType.requetLogin));

        //this then needs to lock until notified
        //the server will reply
        //reply will be handled and user updated
        //then return current user

        return null;
    }

    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

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
        Double total = 0.0;
        for(Dish dish : basket.keySet()){
            total+=(dish.getPrice() * (Integer)basket.get(dish));
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
        Order order = new Order(user, basket);
        orders.add(order);
        comms.send(new Payload(order, TransactionType.requestPurchase));

        //this then needs to lock until notified
        //the server will reply
        //reply will be handled and user updated
        //then return current user

        return null;
    }

    @Override
    public void clearBasket(User user) {
        basket.clear();
    }

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
        comms.send(new Payload(order, TransactionType.requestCancel));
        orders.remove(order);
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {

    }

    @Override
    public void notifyUpdate() {

    }
}
