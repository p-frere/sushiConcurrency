package server;

import common.Dish;
import common.Model;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DishStock implements Runnable{
    private Integer threshold;
    private Map<Model, Integer> stock;
    private Queue<Model> needsCookin;


    public DishStock(Integer threshold){
        this.threshold = threshold;
        stock = new ConcurrentHashMap<>();
        needsCookin = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        //initial check
        //check again after request
        //only check thouse changed in future???
        Iterator it = stock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Model, Integer> pair = (Map.Entry)it.next();
            if(pair.getValue() < threshold){
                addToNeedsCookin(pair.getKey(), threshold-pair.getValue());
            }
        }
    }

    public Dish getNeedsCookin(){
        if(!needsCookin.isEmpty()){
            return (Dish)needsCookin.poll();
        } else {
            return null;
        }
    }

    public void addToNeedsCookin(Model item, Integer amount){
        for(int i = 0; i < amount; i++) {
            needsCookin.add(item);
        }
    }

    public void addToStock(Model item){
        needsCookin.add(item);
    }

    public void serve(Model item, Integer quantity){
        //updates stock
    }
}
