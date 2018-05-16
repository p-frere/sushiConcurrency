package server;
import common.Dish;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores and manages all the dishes
 * It maintains info on the stock and a queue of dish needed that drones can query.
 *
 * Each item stored has an threshold for quantity which it aims to be at consistently
 * If an item is removed this class will request another to be made to take it's place.
 */
public class DishStock implements Runnable{
    private Map<Dish, Number> stock;
    private Set<Dish> restock;
    private Server server;

    //Constructor
    public DishStock(Server server){
        this.server = server;
        stock = new ConcurrentHashMap<>();  //lists stocks of dishes
        restock = new HashSet<>();          //list dishes needed
    }

    @Override
    public void run() {
        while (true) {
            checkStock();
        }
    }

    /**
     * Adds dishes to stock and stores the amount
     * @param dish item to add
     * @param amount the quantity
     */
    public synchronized void addStock(Dish dish, Integer amount){
        if(stock.containsKey(dish)) {
            stock.put(dish, (Integer) stock.get(dish) + amount);
        } else {
            stock.put(dish, amount);
        }
        restock.remove(dish);
        dish.setFetching(false);
    }

    /**
     * Takes stoke from the store one dish at a time
     * removes a dish and returns true if dish can be taken
     * returns false if not
     * @param dish item taken
     * @return flag if successful
     */
    public boolean takeStock(Dish dish){
        if(!stock.containsKey(dish)){
            dish = server.getDish(dish.getName());
        }
        if(stock.get(dish).intValue() > 0) {
            stock.put(dish, stock.get(dish).intValue() - 1); //takes ingredient
            return true;
        } else {
            return false;
        }
    }

    /**
     * checks if restock needed by checking if any
     * item has less than the restock amount,
     * If it is, the item is added to the restock pile
     */
    public void checkStock(){
        Iterator it = stock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Dish, Integer> pair = (Map.Entry)it.next();
            Dish dish = pair.getKey();
            Integer amount = pair.getValue();
            if(amount < dish.getRestockThreshold()){
                addToRestockQueue(dish);
            }
        }
    }

    /**
     * A needed dish is added to restock,
     * This is a set so a dish can only be requested once per time
     * @param dish item required
     */
    public synchronized void addToRestockQueue(Dish dish){
        restock.add(dish);

    }

    /**
     * If a dish can be taken the dish is returned
     * else null is returned.
     * The dish can also not be returned if it has been returned
     * to a different call to stop duplicate cooking
     * @return dish
     */
    public synchronized Dish takeFromRestockQueue(){
        Iterator iter = restock.iterator();
        Dish dish;
        while (iter.hasNext()){
            dish = (Dish) iter.next();
            if (!dish.isFetching()){
                //if it hasn't been fetched yet
                dish.setFetching(true);
                return dish;

            }
        }
        //nothing to be taken
        return null;
    }

    /**
     * Removes stock for when you're removing a dish
     * @param dish
     */
    public void removeStock(Dish dish){
        stock.remove(dish);
        //TODO throw exeption
    }

    //Getters and Setters
    public List<Dish> getDishes(){
        List<Dish> list = new ArrayList<>();
        list.addAll(stock.keySet());
        return list;
    }

    public Map<Dish, Number> getStock() {
        return stock;
    }

    public void setStock(Map<Dish, Number> stock) {
        this.stock = stock;
    }

    public synchronized Set<Dish> getRestock() {
        return restock;
    }

    public synchronized void setRestock(Set<Dish> restock) {
        this.restock = restock;
    }
}
