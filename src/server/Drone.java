package server;

import common.*;

/**
* A drone monitors stock levels of ingredients.
* When these drop below their restocking levels,
* it collects further ingredients from the appropriate supplier.
* The time it takes for this dependa on its speed and the distance to the supplier.
* When it is not collecting stocks, a drone can also deliver customer orders.
 */
public class Drone extends Model implements Runnable {
    private Integer speed;
    private DroneStatus status;
    private transient IngredientsStock ingredientsStock;
    private transient OrderManager orderManager;
    private transient Server server;

    public Drone(Integer speed, Server server){
        this.speed = speed;
        status = DroneStatus.IDLE;
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
        status = DroneStatus.DELIVERING;
        try {
            //represents delivery time
            Thread.sleep((order.getUser().getPostCode().getDistance() / speed) * 4000);
            order.setStatus(OrderStatus.COMPLETE);
            //send order to client
            server.sendToUser(order.getUser(), new Payload(order, TransactionType.deliverOrder));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = DroneStatus.IDLE;
    }


    /**
     * recovers ingredients from supplier
     * @param ingredient
     */
    private void recover(Ingredient ingredient){
        System.out.println(name + ": recovering " + ingredient.getName());
        status = DroneStatus.RECOVERING;
        try {
            //represents recovery time
            Thread.sleep((long)(ingredient.getSupplier().getDistance() / speed) * 4000);
            ingredientsStock.addStock(ingredient, ingredient.getRestockAmount());
            status = DroneStatus.IDLE;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

