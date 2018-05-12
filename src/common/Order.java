package common;

import java.util.*;

//----TODO-------
//Status and ID
//yet to use
//---------------

public class Order extends Model {

    private User user;
    private OrderStatus status;
    private Map<Dish, Number> basket;
    private Integer serverID; //assigned by the thread the order is delivered too
    private Integer clientID;

    public Order(User customer, Map<Dish, Number> basket) {
        this.user = customer;
        this.basket = basket;
        setName(user.getUserName()+" Order");
        status = OrderStatus.PENDING;
    }

    @Override
    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }

    public Set<Dish> getDishes(){
        return basket.keySet();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Integer getOrderDistance(){
        return user.getPostCode().getDistance();
    }

    public Integer getOrderCost(){
        Integer total = 0;
        Iterator it = basket.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Dish dish = (Dish) pair.getKey();
            Integer amount = (Integer)pair.getValue();
            total+=(dish.getPrice()*amount);
        }
        return total;
    }

    public Map<Dish, Number> getBasket() {
        return basket;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Integer getClientID() {
        return clientID;
    }

    public Integer getServerID() {
        return serverID;
    }

    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    public void setServerID(Integer serverID) {
        this.serverID = serverID;
    }
}
