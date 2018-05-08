package server;
import common.Dish;
import common.Ingredient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Stores and manages all the ingredients
 * It maintains info on the stock and a queue of ingredients needed that drones can query
 */
public class IngredientsStock implements Runnable{
    private Map<Ingredient, Number> stock;
    private Queue<Ingredient> restockQueue;
    private Server server;

    public IngredientsStock(Server server){
        stock = new ConcurrentHashMap<>();
        restockQueue = new ConcurrentLinkedQueue<>();
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("IS started");
        checkStock();
    }

    /**
     * Adds ingredients to the stock
     * @param ingredient
     * @param amount
     */
    public void addStock(Ingredient ingredient, Integer amount){
        if(stock.containsKey(ingredient)) {
            stock.put(ingredient, (Integer) stock.get(ingredient) + amount);
        }else {
            stock.put(ingredient, amount);
        }
    }

    /**
     *
     * @param dish
     * @return
     */
    public boolean takeStock(Dish dish){
        //checks if enough ingredients
       Iterator it = dish.getRecipe().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Ingredient ingredient = (Ingredient) pair.getKey();
            Integer amount = (Integer)pair.getValue();

            if (amount > (Integer) stock.get(ingredient)){
                return false;
            }
        }

        //if enough, remove ingredients as they will be used in a dish
        it = dish.getRecipe().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Ingredient ingredient = (Ingredient) pair.getKey();
            Integer amount = (Integer) pair.getValue();
            stock.put(ingredient, (Integer)stock.get(ingredient)-amount);
        }

        //check stock levels
        checkStock();
        return true;
    }

    public void addToRestockQueue(Ingredient item, Integer amount){
//        for(int i = 0; i < amount; i++) {
//            restockQueue.add(item);
//        }
        restockQueue.add(item);
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