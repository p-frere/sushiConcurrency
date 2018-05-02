package client;

import common.*;

import java.util.List;
import java.util.Map;

public class Client implements ClientInterface {
    ClientWindow window;
    ClientComms comms;
    User user;



    public Client(){
        window = new ClientWindow(this);
        comms = new ClientComms(this);

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

        return user;
    }

    @Override
    public List<Postcode> getPostcodes() {
        return null;
    }

    @Override
    public List<Dish> getDishes() {
        return null;
    }

    @Override
    public String getDishDescription(Dish dish) {
        return null;
    }

    @Override
    public Number getDishPrice(Dish dish) {
        return null;
    }

    @Override
    public Map<Dish, Number> getBasket(User user) {
        return null;
    }

    @Override
    public Number getBasketCost(User user) {
        return null;
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {

    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {

    }

    @Override
    public Order checkoutBasket(User user) {
        return null;
    }

    @Override
    public void clearBasket(User user) {

    }

    @Override
    public List<Order> getOrders(User user) {
        return null;
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return false;
    }

    @Override
    public String getOrderStatus(Order order) {
        return null;
    }

    @Override
    public Number getOrderCost(Order order) {
        return null;
    }

    @Override
    public void cancelOrder(Order order) {

    }

    @Override
    public void addUpdateListener(UpdateListener listener) {

    }

    @Override
    public void notifyUpdate() {

    }
}
