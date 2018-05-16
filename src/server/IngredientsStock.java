package server;
import common.Ingredient;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Stores and manages all the ingredients
 * It maintains info on the stock and a queue of ingredients needed that drones can query
 *
 * Each item stored has an threshold for quantity which it aims to be at consistently
 * If an item is removed this class will request another to be made to take it's place.
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
        if(stock.containsKey(ingredient)) {
            stock.put(ingredient, (Integer) stock.get(ingredient) + amount);
        }else {
            stock.put(ingredient, amount);
        }
        restock.remove(ingredient);
        ingredient.setFetching(false);
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
        Iterator iter = restock.iterator();
        Ingredient ingredient;
        while (iter.hasNext()){
            ingredient = (Ingredient)iter.next();
            if (!ingredient.isFetching()){
                //if it hasn't been fetched yet
                return ingredient;
            }
        }
        //nothing to be taken atm
        return null;
    }


    public synchronized void removeStock(Ingredient ingredient){
        stock.remove(ingredient);
        //todo unable to delete exception
    }

    //getters and setters
    public synchronized List<Ingredient> getIngredients(){
        List<Ingredient> list = new ArrayList<>();
        list.addAll(stock.keySet());
        return list;
    }

    public Map<Ingredient, Number> getStock() {
        return stock;
    }

    public void setStock(Map<Ingredient, Number> stock) {
        this.stock = stock;
    }

    public synchronized Set<Ingredient> getRestock() {
        return restock;
    }

    public synchronized void setRestock(Set<Ingredient> restock) {
        this.restock = restock;
    }

    public synchronized Map<Ingredient, Number> getStockLevels(){
        return stock;
    }

}
