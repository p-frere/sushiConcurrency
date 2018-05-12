package server;

import common.*;

/**
A drone monitors stock levels of ingredients.
When these drop below their restocking levels,
it collects further ingredients from the appropriate supplier.
The time it takes for this dependa on its speed and the distance to the supplier.
When it is not collecting stocks, a drone can also deliver customer orders.
 */
public class Drone extends Model implements Runnable {
    private Integer speed;
    private DroneStatus status;
    private IngredientsStock ingredientsStock;
    private OrderManager orderManager;
    private Server server;

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
            ingredient = ingredientsStock.takeFromRestockQueue();
            if (ingredient != null){
                recover(ingredient);
            }

            order = orderManager.takeOrder();
            if (order != null) {
                status = DroneStatus.DELIVERING;
                try {
                    Thread.sleep((order.getUser().getPostCode().getDistance() / speed) * 2); //how long?
                    order.setStatus(OrderStatus.COMPLETE);
                    server.deliverOrder(order);
                } catch (InterruptedException e) {
                    System.out.println("errors when deliver sleeping");
                }
                status = DroneStatus.IDLE;
            }

        }
    }

    //recover ingredients from supplier
    public void recover(Ingredient ingredient){
        System.out.println(name + " recovering " + ingredient.getName());
        status = DroneStatus.RECOVERING;
        try {
            Thread.sleep((long)(ingredient.getSupplier().getDistance() / speed) * 1000);
            ingredientsStock.addStock(ingredient, ingredient.getRestockAmount());
            status = DroneStatus.IDLE;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

//    //delivers dish to users
//    public void deliver(Order order) {
//
//    }

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

