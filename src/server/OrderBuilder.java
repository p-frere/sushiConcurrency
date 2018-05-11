package server;

import common.Dish;
import common.Order;
import java.util.HashMap;
import java.util.Map;

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
            Order order = orderManager.takeFromIncomingOrders();
            if (order != null) {
                System.out.println("order is collecting for an order");
                boolean orderMade = false;
                Map<Dish, Number> basket = new HashMap<>(order.getBasket());
                Map<Dish, Number> obtained = new HashMap<>(order.getBasket());

                for(Dish dish : obtained.keySet()){
                    obtained.put(dish, 0);
                }

                while (!orderMade) {
                    for(Dish dish : obtained.keySet()){
                        if(basket.get(dish).intValue() > obtained.get(dish).intValue()){
                            if (dishStock.takeStock(dish)){
                                obtained.put(dish, obtained.get(dish).intValue()+1);
                            }
                        }
                    }

                    if(basket.equals(obtained)){
                        orderManager.completeOrder(order);
                        orderMade = true;
                    }
                }
            }
        }
    }
}
