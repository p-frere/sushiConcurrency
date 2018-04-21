package server;

import common.Ingredient;
import common.Model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ingredientsStock implements Runnable{
    private Integer threshold;
    private Map<Model, Integer> stock;

    public ingredientsStock(Integer threshold){
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
        //calls suplier
    }

    public void serve(Model item, Integer quantity){
        //updates stock
    }
}
