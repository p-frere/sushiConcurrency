package server;

import common.Dish;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DishStock implements Runnable{
    private Integer threshold;
    private Map<Dish, Integer> stock;
    private Queue<Dish> requestedDish;


    public DishStock(Integer threshold){
        this.threshold = threshold;
        stock = new ConcurrentHashMap<>();
        requestedDish = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        //initial check
        //check again after request
        //only check thouse changed in future???
        checkStock();
    }


    public void addStock(Dish dish){
        requestedDish.add(dish);
    }

    public Dish takeStock(Dish dish, Integer quantity){
        if (stock.get(dish)-quantity > 0) {
            stock.put(dish, stock.get(dish) - quantity);
            return dish;
        } else {
            return null;
            //what is the thresh hold?
            //do I need to put more requests on queue
        }
    }

    public Dish getRequestedDish(){
        if(!requestedDish.isEmpty()){
            return (Dish)requestedDish.poll();
        } else {
            return null;
        }
    }

    public void addToRequestedDish(Dish dish, Integer amount){
        for(int i = 0; i < amount; i++) {
            requestedDish.add(dish);
        }
    }

    public void checkStock(){
        Iterator it = stock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Dish, Integer> pair = (Map.Entry)it.next();
            if(pair.getValue() < threshold){
                addToRequestedDish(pair.getKey(), threshold-pair.getValue());
            }
        }
    }
}
