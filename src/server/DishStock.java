package server;
import common.Dish;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DishStock implements Runnable{
    private Map<Dish, Number> stock;
    private Queue<Dish> restockQueue;
    private Server server;

    public DishStock(Server server){
        this.server = server;
        stock = new ConcurrentHashMap<>();              //lists stocks of dishes
        restockQueue = new ConcurrentLinkedQueue<>();  //list of dishes that need to be made
    }

    @Override
    public void run() {
        //initial check
        //check again after request
        //only check thouse changed in future???
        checkStock();
    }

    public void addStock(Dish dish, Integer amount){
        if(stock.containsKey(dish)) {
            stock.put(dish, (Integer) stock.get(dish) + amount);
        } else {
            stock.put(dish, amount);
        }
    }

    public Dish takeStock(Dish dish, Integer quantity){
        if ((Integer)stock.get(dish)-quantity > 0) {
            stock.put(dish, (Integer)stock.get(dish) - quantity);
            checkStock();
            return dish;
        } else {
            return null;
        }
    }


    public void addToRestockQueue(Dish dish, Integer amount){
        for(int i = 0; i < amount; i++) {
            restockQueue.add(dish);
        }
    }

    public Dish getFromRestockQueue(){
        if(!restockQueue.isEmpty()){
            return restockQueue.poll();
        } else {
            return null;
        }
    }

    //checks if restock needed
    public void checkStock(){
        Iterator it = stock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Dish, Integer> pair = (Map.Entry)it.next();
            Dish dish = (Dish) pair.getKey();
            Integer amount = (Integer) pair.getValue();
            if(amount < dish.getRestockThreshold()){
                addToRestockQueue(dish, dish.getRestockThreshold()-amount);
            }
        }
    }

    public void removeStock(Dish dish){
        stock.remove(dish);
        //todo unable to delete exception
    }

    public List<Dish> getDishes(){
        List<Dish> list = new ArrayList<>();
        for (Dish dish : stock.keySet()){
            list.add(dish);
        }

        return list;
    }

    public Map<Dish, Number> getStockLevels() {
        return stock;
    }
}
