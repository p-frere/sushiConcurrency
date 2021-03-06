package server;

import common.Dish;
import common.Order;
import java.util.HashMap;
import java.util.Map;

/**
 * OrderBuilder collects all the dishes required for the current order
 * when it has enough to match the order it returns the completed order to being on the
 * next.
 */
public class OrderBuilder implements Runnable{
    private Server server;
    private DishStock dishStock;
    private OrderManager orderManager;

    public OrderBuilder(Server server){
        this.server = server;
        this.dishStock = server.getDishStock();
        this.orderManager = server.getOrderManager();
    }

    @Override
    public void run() {
        while (true) {
            //checks every 10th of a second
            //This avoid strain on the computer
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Order order = orderManager.takeFromIncomingOrders();
            if (order != null) {
                System.out.println("collecting for a new order");
                boolean orderMade = false;
                Map<Dish, Number> basket = new HashMap<>(order.getBasket());    //What needs to be obtained
                Map<Dish, Number> obtained = new HashMap<>(order.getBasket());  //What has been obtained

                for(Dish dish : obtained.keySet()){    //sets all obtained equal to 0
                    obtained.put(dish, 0);
                }

                while (!orderMade) {    //begins to collect items
                    for(Dish dish : obtained.keySet()){
                        if(basket.get(dish).intValue() > obtained.get(dish).intValue()){
                            if (dishStock.takeStock(dish)){
                                obtained.put(dish, obtained.get(dish).intValue()+1);
                            }
                        }
                    }

                    //check if all items are obtain or it has to collect more
                    if(basket.equals(obtained)){
                        orderManager.completeOrder(order);
                        orderMade = true;
                    }
                }
            }
        }
    }
}
