package common;

import server.Server;
import common.Dish;

import java.util.*;

//----TODO-------
//Status and ID
//yet to use
//---------------

public class Order extends Model {

    private User user;
    private OrderStatus status;
    public Map<Dish, Number> basket;

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

    //turns the map into a list of
    public List<Dish> listItems(){
        List<Dish> list = new ArrayList<>();
        Iterator it = basket.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Dish dish = (Dish) pair.getKey();
            Integer amount = (Integer) pair.getValue();
            for (int i = 0; i < amount; i++){
                list.add(dish);
            }
        }
        return list;
    }

    public User getUser() {
        return user;
    }

    public Set<Dish> getDishes(){
        return basket.keySet();
    }

    //get dish quantilty
    public Integer geetDishQuantity(Dish dish){
        return basket.get(dish).intValue();
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
    //Think of an order as something a drone will carry out
    // so it has a list of dishes
    // with corresponding quantities
    // and a postcode which it will be delivered to
}
