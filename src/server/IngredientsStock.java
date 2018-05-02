package server;

import common.Dish;
import common.Ingredient;
import common.Model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IngredientsStock implements Runnable{
    private Integer threshold;
    private Map<Ingredient, Integer> stock;
    private Queue<Ingredient> restockQueue;

    public IngredientsStock(Integer threshold){
        this.threshold = threshold;
        stock = new ConcurrentHashMap<>();
        restockQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        //initial check
        //check again after request
        //only check thouse changed in future???
        checkStock();

    }

    public void addStock(Ingredient ingredient, Integer amount){
        stock.put(ingredient, stock.get(ingredient)+amount);
    }

    public boolean takeStock(Dish dish){
        //checks if makable
       Iterator it = dish.getRecipe();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            Ingredient ingredient = (Ingredient) pair.getKey();
            Double amount = (Double)pair.getValue();

            if (amount > stock.get(ingredient)){
                return false;
            }
        }
        //and remove ingrients
        it = dish.getRecipe();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Ingredient ingredient = (Ingredient) pair.getKey();
            Integer amount = (Integer) pair.getValue();
            stock.put(ingredient, stock.get(ingredient)-amount);
        }
        checkStock();
        return true;
    }

    public void addToRestockQueue(Ingredient item){
       restockQueue.add(item);
    }

    public Ingredient getFromRestockQueue(){
        if(!restockQueue.isEmpty()){
            return restockQueue.poll();
        } else {
            return null;
        }
    }

    public void checkStock(){
        Iterator it = stock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Ingredient, Integer> pair = (Map.Entry)it.next();
            if(pair.getValue() < threshold){
                addToRestockQueue(pair.getKey());
            }
        }
    }

}

/*
public synchronized Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}
 */


/*

Write appropriate classes to keep track of ingredients and prepared dishes that are currently held in stock by the business.

For each ingredient and dish, there should be a restocking threshold (at which restocking occurs)
and a restocking amount.

 Falling below this level means that new ingredients should be ordered or new dishes should be prepared


 */