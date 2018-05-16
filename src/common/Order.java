package common;

import java.util.*;

/**
 * Order holds the dishes and quantity as well as the user who made the order
 */
public class Order extends Model {

    private User user;
    private OrderStatus status;
    private Map<Dish, Number> basket;   //the dishes and their amounts
    private Integer orderID;

    //Constructor
    public Order(User customer, Map<Dish, Number> basket) {
        this.user = customer;
        this.basket = basket;
        setName(user.getUserName()+" Order");
        status = OrderStatus.PENDING;
    }

    //Getters and Setters
    @Override
    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void setBasket(Map<Dish, Number> basket) {
        this.basket = basket;
    }

    public Number geetDishAmount(Dish dish){
        return basket.get(dish);
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer clientID) {
        this.orderID = orderID;
    }

}
