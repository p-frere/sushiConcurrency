package server;
import common.Ingredient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Stores and manages all the ingredients
 * It maintains info on the stock and a queue of ingredients needed that drones can query
 */
public class IngredientsStock implements Runnable{
    private Map<Ingredient, Number> stock;
    private Set<Ingredient> restock;
    private Server server;

    public IngredientsStock(Server server){
        stock = new ConcurrentHashMap<>();
        restock = new HashSet<>();
        this.server = server;

    }

    @Override
    public void run() {
        while(true) {
            checkStock();
        }
    }

    /**
     * Adds ingredients to the stock
     * @param ingredient
     * @param amount
     */
    public synchronized void addStock(Ingredient ingredient, Integer amount){
        //System.out.println(ingredient.getName() + " x 10 added to stock");
        if(stock.containsKey(ingredient)) {
            stock.put(ingredient, (Integer) stock.get(ingredient) + amount);
        }else {
            stock.put(ingredient, amount);
        }
    }


    public synchronized boolean takeStock(Ingredient ingredient){
        if(stock.get(ingredient).intValue() > 0) {
            stock.put(ingredient, stock.get(ingredient).intValue() - 1); //takes ingredient
            return true;
        } else {
            return false;
        }
    }


        //checks if restock is needed
    public synchronized void checkStock(){
        Iterator it = stock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Ingredient, Integer> pair = (Map.Entry)it.next();
            Ingredient ingredient = pair.getKey();
            Integer amount = pair.getValue();
            if(amount < ingredient.getRestockThreshold()){
                addToRestockQueue(ingredient);
            }
        }
    }

    public synchronized void addToRestockQueue(Ingredient item){
        restock.add(item);
    }

    public synchronized Ingredient takeFromRestockQueue(){
        if(!restock.isEmpty()){
            Iterator iter = restock.iterator();
            Ingredient ingredient = (Ingredient)iter.next();
            iter.remove();
            return ingredient;
        } else {
            return null;
        }
    }

    public synchronized void removeStock(Ingredient ingredient){
        stock.remove(ingredient);
        //todo unable to delete exception
    }

    public synchronized List<Ingredient> getIngredients(){
        List<Ingredient> list = new ArrayList<>();
        list.addAll(stock.keySet());
        return list;
    }

    public synchronized Map<Ingredient, Number> getStockLevels(){
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