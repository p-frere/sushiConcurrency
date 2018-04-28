package server;
import java.util.Random;
import common.*;

public class Staff extends Model implements Runnable {
    private String status;
    private DishStock dishStock;
    private IngredientsStock ingredientsStock;
    private String name;

    public Staff(String name, DishStock dishStock, IngredientsStock ingredientsStock){
        this.name = name;
        this.dishStock = dishStock;
        this.ingredientsStock = ingredientsStock;
    }

    @Override
    public void run() {
        while (true) { //forever, for the works are slaves and get not holidays
            Dish dish = dishStock.getNeedsCookin();
            //if no dishes are in queue try again
            if (dish != null) {
                if(ingredientsStock.canIMake(dish)){  //should also remove stock if true
                    getCookin(dish);
                }else {
                    //if you can't cook now add to back of queue
                    dishStock.addToNeedsCookin(dish, 1);
                }
            }
        }
    }


    public void getCookin(Dish dish) {
            status = "Cooking up a storm";
            getStatus(); //prints

            Random rand = new Random();
            try {

                Thread.sleep((rand.nextInt(40)+20)*10); // Simulates the time to prepare
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            dishStock.addToStock(dish);
            status = "idle";
    }

    @Override
    public String getName() {
        return name;
    }

    public void getStatus(){
        if (!status.equals("idle"))
            System.out.println(getName() + ": I am " + status);
        else
            System.out.println(getName() + ": I am idle");
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

A staff member should be able to return a text status which indicates its current job. If the staff member is performing no action, this should simply return "Idle".



 */