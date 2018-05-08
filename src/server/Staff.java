package server;
import java.util.Random;
import common.*;

public class Staff extends Model implements Runnable {
    private StaffStatus status;
    private Server server;
    private DishStock dishStock;
    private IngredientsStock ingredientsStock;
    private String name;

    public Staff(String name, Server server){
        setName(name);
        status = StaffStatus.IDLE;
        this.server = server;
        this.dishStock = server.getDishStock();
        this.ingredientsStock = server.getIngredientsStock();
    }

    @Override
    public void run() {

//        while (true) { //forever, for the works are slaves and get not holidays
//            Dish dish = dishStock.getFromRestockQueue();
//            //if no dishes are in queue try again
//            if (dish != null) {
//                if(ingredientsStock.takeStock(dish)){  //should also remove stock if true
//                    getCookin(dish);
//                }else {
//                    return;
//                }
//            }
//        }

    }

    public void getCookin(Dish dish) {
        status = StaffStatus.COOKING;
        Random rand = new Random();
        try {
            Thread.sleep((rand.nextInt(40)+20)*10); // Simulates the time to prepare
            dishStock.addStock(dish, 1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = StaffStatus.IDLE;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getStatus(){
        return status.toString();
    }
}


/*
Write a Staff class that represents a member of the kitchen staff
and that can be run as a Java Thread and extends the base Model class.

When running, an instance of this thread should monitor the stock levels of dishes.
Should any fall below their restocking levels and there are sufficient ingredients to
satisfy the recipe of the dish, the thread should prepare a new dish
(using up the required ingredients).

This should take a random amount of time between some specified lower and upper bounds
(for example 20-60 seconds). Make sure all your classes are synchronized appropriately,
so that multiple kitchen staff threads can operate concurrently.

A staff member should be able to return a text status which indicates its current job.
If the staff member is performing no action, this should simply return "Idle".
 */