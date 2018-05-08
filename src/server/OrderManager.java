package server;
import common.Order;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The role of Order manager is to store incoming and outgoing orders
 * It requests new incoming orders to be made and allows drones to take out going orders
 */
public class OrderManager implements Runnable{
    private Queue<Order> incomingOrders;    //orders that need (need to produced)
    private Queue<Order> outgoingOrders;    //orders that we have (need to be droned out)
    private DishStock dishStock;
    private Server server;

    public OrderManager(Server server){
        incomingOrders =  new ConcurrentLinkedQueue<>();
        outgoingOrders = new ConcurrentLinkedQueue<>();
        this.server = server;
        dishStock = server.getDishStock();
    }

    @Override
    public void run() {
        //requestDishes();
    }

    /**
     * Adds an order to incoming orders
     * @param order
     */
    public void addOrder(Order order){
        incomingOrders.add(order);
        requestDishes();
    }

    /**
     * Takes an order from outgoing orders
     * to deliver them
     * @return
     */
    public Order takeOrder(){
        if(outgoingOrders.isEmpty()){
            return null;
        } else {
            return outgoingOrders.peek();
        }
    }

    /**
     * Requests the the first order in incoming Orders
     * if the required dishes are present it puts them in outgoing orders
     */
    private void requestDishes(){
        Order order = incomingOrders.peek();
        if (order ==null){
            return;
        }

        if (dishStock.takeStock(order)) {
            outgoingOrders.add(incomingOrders.poll());
            requestDishes();
        } else {
            return;
        }
    }

    /**
     * Checks through both queue for an order to remove
     * @param order
     */
    public void cancelOrder(Order order){
        for(Iterator<Order> it = incomingOrders.iterator(); it.hasNext();){
            if (it.next() == order){
                it.remove();
                return;
            }
        }
        for(Iterator<Order> it = outgoingOrders.iterator(); it.hasNext();){
            if (it.next() == order){
                it.remove();
                return;
            }
        }
        System.out.println("WARNING: cancelOrder() removed nothing");
    }
}
