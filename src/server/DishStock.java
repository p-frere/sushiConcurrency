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
    private Queue<Dish> restockQueue;


    public DishStock(Integer threshold){
        this.threshold = threshold;
        stock = new ConcurrentHashMap<>();              //lists stocks of dishes
        restockQueue = new ConcurrentLinkedQueue<>();  //list of dishes that need to be made
    }

    @Override
    public void run() {
        //initial check
        //check again after request
        //only check thouse changed in future???
        checkStock();
    }


    public void addStock(Dish dish, Integer amount){
        stock.put(dish, stock.get(dish)+amount);
    }

    public Dish takeStock(Dish dish, Integer quantity){
        if (stock.get(dish)-quantity > 0) {
            stock.put(dish, stock.get(dish) - quantity);
            checkStock();
            return dish;
        } else {
            return null;
            //what is the thresh hold?
            //do I need to put more requests on queue
        }
    }

    public void addToRestockQueue(Dish dish, Integer amount){
        for(int i = 0; i < amount; i++) {
            restockQueue.add(dish);
        }
    }

    public Dish getFromRestockQueue(){
        if(!restockQueue.isEmpty()){
            return restockQueue.poll();
        } else {
            return null;
        }
    }

    public void checkStock(){
        Iterator it = stock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Dish, Integer> pair = (Map.Entry)it.next();
            if(pair.getValue() < threshold){
                addToRestockQueue(pair.getKey(), threshold-pair.getValue());
            }
        }
    }
}
