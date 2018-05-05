package server;
import common.Dish;
import common.Ingredient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IngredientsStock implements Runnable{
    private Map<Ingredient, Number> stock;
    private Queue<Ingredient> restockQueue;

    public IngredientsStock(){
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
        stock.put(ingredient, (Integer)stock.get(ingredient)+amount);
    }

    public boolean takeStock(Dish dish){
        //checks if makable
       Iterator it = dish.getRecipe().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            Ingredient ingredient = (Ingredient) pair.getKey();
            Integer amount = (Integer)pair.getValue();

            if (amount > (Integer) stock.get(ingredient)){
                return false;
            }
        }
        //and remove ingrients
        it = dish.getRecipe().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Ingredient ingredient = (Ingredient) pair.getKey();
            Integer amount = (Integer) pair.getValue();
            stock.put(ingredient, (Integer)stock.get(ingredient)-amount);
        }
        checkStock();
        return true;
    }

    public void addToRestockQueue(Ingredient item, Integer amount){
        for(int i = 0; i < amount; i++) {
            restockQueue.add(item);
        }
    }

    public Ingredient getFromRestockQueue(){
        if(!restockQueue.isEmpty()){
            return restockQueue.poll();
        } else {
            return null;
        }
    }

    //checks if restock is needed
    public void checkStock(){
        Iterator it = stock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Ingredient, Integer> pair = (Map.Entry)it.next();
            Ingredient ingredient = (Ingredient) pair.getKey();
            Integer amount = (Integer) pair.getValue();
            if(amount < ingredient.getRestockThreshold()){
                addToRestockQueue(ingredient, ingredient.getRestockThreshold()-amount);
            }
        }
    }


    public void removeStock(Ingredient ingredient){
        stock.remove(ingredient);
        //todo unable to delete exception
    }

    public List<Ingredient> getIngredients(){
        List<Ingredient> list = new ArrayList<>();
        for (Ingredient item : stock.keySet()){
            list.add(item);
        }
        return list;
    }

    public Map<Ingredient, Number> getStockLevels(){
        return stock;
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