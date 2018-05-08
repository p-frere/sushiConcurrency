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
    }

    @Override
    public void run() {
        System.out.println(name + " started");
        while (true) {
//            Order order = orderManager.takeOrder();
//            //if no dishes are in queue try again
//            if (order != null) {
//                deliver(order, order.getUser());
//
//            }

            Ingredient ingredient = ingredientsStock.getFromRestockQueue();
            if (ingredient != null){
                recover(ingredient);
            }

        }
    }

    //recover ingredients from supplier
    public void recover(Ingredient ingredient){
        System.out.println(name + " recovering " + ingredient.getName());
        status = DroneStatus.RECOVERING;
        System.out.println(getName() + "flying out");
        try {
            Thread.sleep((long)(ingredient.getSupplier().getDistance() / speed) * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + "flying in");
        ingredientsStock.addStock(ingredient, ingredient.getRestockAmount());
        status = DroneStatus.IDLE;
    }

    //delivers dish to users
    public void deliver(Order order, User user) {
        status = DroneStatus.DELIVERING;
        try {
            //"You can assume a fixed distance for each customer postcode"
            Thread.sleep((user.getPostCode().getDistance() / speed) * 2); //how long?
        } catch (InterruptedException e) {
            System.out.println("errors when deliver sleeping");
        }
        status = DroneStatus.IDLE;
        //todo server.send
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

