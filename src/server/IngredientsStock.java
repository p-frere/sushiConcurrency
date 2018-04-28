package server;

import common.Dish;
import common.Ingredient;
import common.Model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IngredientsStock implements Runnable{
    private Integer threshold;
    private Map<Model, Integer> stock;

    public IngredientsStock(Integer threshold){
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

    public boolean canIMake(Dish dish){
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
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            Ingredient ingredient = (Ingredient) pair.getKey();
            Integer amount = (Integer) pair.getValue();
            stock.put(ingredient, stock.get(ingredient)-amount);
        }
        return true;
    }

    public void restock(Model item){
        //calls suplier
    }

}


/*

Write appropriate classes to keep track of ingredients and prepared dishes that are currently held in stock by the business.

For each ingredient and dish, there should be a restocking threshold (at which restocking occurs)
and a restocking amount.

 Falling below this level means that new ingredients should be ordered or new dishes should be prepared


 */