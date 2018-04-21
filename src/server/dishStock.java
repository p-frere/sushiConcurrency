package server;

import common.Model;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class dishStock implements Runnable{
    private Integer threshold;
    private Map<Model, Integer> stock;

    public dishStock(Integer threshold){
        this.threshold = threshold;
        stock = new ConcurrentHashMap<>();
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
                restock(pair.getKey());
            }
        }
    }

    public void restock(Model item){
        //sets a new thread to make dish
    }

    public void serve(Model item, Integer quantity){
        //updates stock
    }
}
