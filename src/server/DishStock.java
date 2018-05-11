package server;
import common.Dish;
import common.Ingredient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DishStock implements Runnable{
    private Map<Dish, Number> stock;
    private Set<Dish> restock;
    private Server server;

    public DishStock(Server server){
        this.server = server;
        stock = new ConcurrentHashMap<>();              //lists stocks of dishes
        restock = new HashSet<>();
    }

    @Override
    public void run() {
        while (true) {
            checkStock();
        }
    }

    public void addStock(Dish dish, Integer amount){
        if(stock.containsKey(dish)) {
            stock.put(dish, (Integer) stock.get(dish) + amount);
        } else {
            stock.put(dish, amount);
        }
        restock.remove(dish);
        dish.setFetching(false);
    }

    public boolean takeStock(Dish dish){
        if(stock.get(dish).intValue() > 0) {
            stock.put(dish, stock.get(dish).intValue() - 1); //takes ingredient
            return true;
        } else {
            return false;
        }
    }

    //checks if restock needed
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


    public synchronized void addToRestockQueue(Dish dish){
        restock.add(dish);

    }


//    public synchronized Dish takeFromRestockQueue(){
//        if(!restock.isEmpty()){
//            Iterator iter = restock.iterator();
//            Dish dish = (Dish) iter.next();
//            iter.remove();
//            return dish;
//        } else {
//            return null;
//        }
//    }

    public synchronized Dish takeFromRestockQueue(){
        Iterator iter = restock.iterator();
        Dish dish;
        while (iter.hasNext()){
            dish = (Dish) iter.next();
            if (!dish.isFetching()){
                //if it hasn't been fetched yet
                return dish;
            }
        }
        //nothing to be taken atm
        return null;
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
