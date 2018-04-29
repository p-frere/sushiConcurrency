package common;

import java.util.HashMap;
import java.util.Iterator;

//----TODO-------
//Status and ID
//yet to use
//shopping basket is diffrent class
//---------------

public class Order extends Model {

    private String ID;
    private User user;
    private HashMap<Dish, Integer> items;
    private String status="Pending";


    public Order(User customer) {
        this.user = customer;
        this.items = new HashMap<>();
    }

    public void addDish(Dish dish, Integer amount) {
        items.put(dish, amount);
    }

    public Iterator getDishes(){
        return items.entrySet().iterator();
    }

    @Override
    public String getName() {
        return null;
    }
}
