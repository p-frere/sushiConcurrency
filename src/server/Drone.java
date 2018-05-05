package server;

import common.*;

public class Drone extends Model implements Runnable {
    private Integer speed;
    private DroneStatus status;
    private IngredientsStock ingredientsStock;
    private DishStock dishStock;

    public Drone(Integer speed){
        this.speed = speed;
        status = DroneStatus.IDLE;
    }

    @Override
    public void run() {
        while (true) { //forever

        }
    }

    //recover ingredients from supplier
    public void recover(Ingredient ingredient){
        status = DroneStatus.RECOVERING;
        try {
            Thread.sleep((long)(ingredient.getSupplier().getDistance() / speed)); //Milliseconds??
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = DroneStatus.IDLE;
    }

    //delivers dish to users
    public void deliver(Dish dish, User user) { //user?
        status = DroneStatus.DELIVERING;
        try {
            Thread.sleep(2000); //how long?
            //todo talk to clint?
        } catch (InterruptedException e) {
            System.out.println("errors when deliver sleeping");
        }
        status = DroneStatus.IDLE;
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
        return null;
    }
}

/*
A drone should monitor stock levels of ingredients.
When these drop below their restocking levels,
it should collect further ingredients from the appropriate supplier.
The time it will take for this will depend on its speed and the distance to the supplier.

When it is not collecting stocks, a drone can also deliver customer orders.
You can assume a fixed distance for each customer postcode,
and along with the speed of the drone, this will determine the delivery time.

Again, ensure your classes are appropriately synchronized,
so that multiple drones can run concurrently.

A drone should be able to return a text status which indicates its current job.
If the drone is performing no action, this should simply return "Idle".



 */
