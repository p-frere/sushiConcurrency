package server;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import common.*;

public class Staff extends Model implements Runnable {
    private StaffStatus status;
    private Server server;
    private DishStock dishStock;
    private IngredientsStock ingredientsStock;

    public Staff(String name, Server server){
        setName(name);
        status = StaffStatus.IDLE;
        this.server = server;
        this.dishStock = server.getDishStock();
        this.ingredientsStock = server.getIngredientsStock();
    }

    @Override
    public void run() {

        while (true) { //forever, for the works are slaves and get not holidays

            Dish dish = dishStock.takeFromRestockQueue();
            if (dish != null) {
                //System.out.println(getName() + "is collecting for " + dish.getName());

                boolean dishMade = false;
                Map<Ingredient, Number> recipe = new HashMap<>(dish.getRecipe()); //remove to partial work
                Map<Ingredient, Number> obtained = new HashMap<>(dish.getRecipe());
                for(Ingredient ingredient : obtained.keySet()){
                    obtained.put(ingredient, 0);
                }

                while (!dishMade) {
                    for(Ingredient ingredient : obtained.keySet()){
                        if(recipe.get(ingredient).intValue() > obtained.get(ingredient).intValue()){
                            if (ingredientsStock.takeStock(ingredient)){
                                obtained.put(ingredient, obtained.get(ingredient).intValue()+1);
                            }
                        }
                    }

                    if(recipe.equals(obtained)){
                        getCookin(dish);
                        dishMade = true;
                    }
                }
            }
        }
    }

    public void getCookin(Dish dish) {
        status = StaffStatus.COOKING;
        Random rand = new Random();
        try {
            System.out.println(getName() + ": cooking");
            Thread.sleep((rand.nextInt(40)+20)*100); // Simulates the time to prepare
            dishStock.addStock(dish, 10);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + ": finished");
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