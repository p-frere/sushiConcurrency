package server;

import common.Order;
import common.OrderStatus;
import java.util.Iterator;;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The role of Order manager is to store incoming and outgoing orders
 * It requests new incoming orders to be made and allows drones to take out going orders
 */
public class OrderManager implements Runnable{
    private Queue<Order> incomingOrders;    //orders that need (need to produced)
    private Queue<Order> outgoingOrders;    //orders that we have (need to be droned out)
    private Server server;

    //Constructor
    public OrderManager(Server server){
        incomingOrders =  new ConcurrentLinkedQueue<>();
        outgoingOrders = new ConcurrentLinkedQueue<>();
        this.server = server;
    }

    @Override
    public void run() {
    }

    /**
     * Adds an order to incoming orders
     * @param order
     */
    public void addOrder(Order order){
        incomingOrders.add(order);
        System.out.println("-- order added");
        //order.setStatus(OrderStatus.COOKINGQUEUE);
    }

    /**
     * called when the makeup of an order is complete
     * adds to the outgoing orders queue
     * @param order
     */
    public void completeOrder(Order order){
        outgoingOrders.add(order);
        order.setStatus(OrderStatus.DELIVERYQUEUE);
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
            return outgoingOrders.poll();
        }
    }

    /**
     * Takes an order in order to make it
     * @return
     */
    public Order takeFromIncomingOrders(){
        if(!incomingOrders.isEmpty()){
            return incomingOrders.poll();
        } else {
            return null;
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

    //gettos and settos

    public Queue<Order> getIncomingOrders() {
        return incomingOrders;
    }

    public void setIncomingOrders(Queue<Order> incomingOrders) {
        this.incomingOrders = incomingOrders;
    }

    public Queue<Order> getOutgoingOrders() {
        return outgoingOrders;
    }

    public void setOutgoingOrders(Queue<Order> outgoingOrders) {
        this.outgoingOrders = outgoingOrders;
    }
}
