package server;

import common.*;

/**
* A drone monitors the ingredients that need to be restocked.
* When it can, it collects further ingredients from the appropriate supplier.
* The time it takes for this depends on its speed and the distance to the supplier.
*
 * When it is not collecting stocks, a drone can also deliver customer orders.
 * This also has a dependant time.
 */
public class Drone extends Model implements Runnable {
    private Integer speed;
    private DroneStatus status;
    private transient IngredientsStock ingredientsStock;
    private transient OrderManager orderManager;
    private transient Server server;

    public Drone(Integer speed, Server server){
        this.speed = speed;
        status = DroneStatus.IDLE;  //signals the drones current job
        setName("droneSpeed"+speed);
        ingredientsStock = server.getIngredientsStock();
        orderManager =  server.getOrderManager();
        this.server = server;
    }

    @Override
    public void run() {
        Ingredient ingredient;
        Order order;

        while (true) {
            try {
                //checks every 10th of a second
                //This avoid strain on the computer
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //gets an item from a restock queue to restock
            ingredient = ingredientsStock.takeFromRestockQueue();
            if (ingredient != null){
                recover(ingredient);
            }

            //gets an order from the order queue to deliver
            order = orderManager.takeOrder();
            if (order != null) {
                deliver(order);
            }
        }
    }

    /**
     * Attempts to deliver order to a client
     * @param order
     */
    public void deliver(Order order){
        setStatus(DroneStatus.DELIVERING);
        try {
            //represents delivery time
            Thread.sleep(3000 + (order.getUser().getPostCode().getDistance() / speed));
            order.setStatus(OrderStatus.COMPLETE);
            //send order to client
            server.sendToUser(order.getUser(), new Payload(order, TransactionType.deliverOrder));
            server.save();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setStatus(DroneStatus.IDLE);
    }

    /**
     * recovers ingredients from supplier
     * @param ingredient
     */
    private void recover(Ingredient ingredient){
        System.out.println(name + ": recovering " + ingredient.getName());
        setStatus(DroneStatus.RECOVERING);
        try {
            //represents recovery time
            Thread.sleep(3000 + (long)(ingredient.getSupplier().getDistance() / speed));
            ingredientsStock.addStock(ingredient, ingredient.getRestockAmount());
            setStatus(DroneStatus.IDLE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void setStatus(DroneStatus status){
        this.status = status;
        notifyUpdate();
    }

    //setta and getta
    public Integer getSpeed() {
        return speed;
    }

    public DroneStatus getStatus() {
        return status;
    }

    @Override
    public String getName() {
        return name;
    }
}

