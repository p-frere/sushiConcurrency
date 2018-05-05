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


    public Order(User customer, Map<Dish, Number> basket) {
        this.user = customer;
        this.basket = basket;
        status = OrderStatus.PENDING;
    }

    @Override
    public String getName() {
        return "New Order";
    }

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

    public OrderStatus getStatus() {
        return status;
    }

    public Integer getOrderDistance(){
        return user.getPostCode().getDistance();
    }

    public Integer getOrderCost(){
        Integer total = 0;
        for (Dish dish : basket.keySet()){
            total+=(server*(Integer) basket.get(dish));
        }
//        Iterator it = basket.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            Dish dish = (Dish) pair.getKey();
//            Integer amount = (Integer)pair.getValue();
//            total+=(dish.getPrice()*amount);
//        }
        return total;
    }

    //Think of an order as something a drone will carry out
    // so it has a list of dishes
    // with corresponding quantities
    // and a postcode which it will be delivered to
}
