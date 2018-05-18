package server;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import common.*;

/**
 * Represents a member of the kitchen staff
 * Tries to take dishes out of the restock queue when available when there are sufficient ingredients to
 satisfy the recipe of the dish, the staff member should prepare a new dish
 */
public class Staff extends Model implements Runnable, Serializable {
    private StaffStatus status;
    private transient Server server;
    private transient DishStock dishStock;
    private transient IngredientsStock ingredientsStock;

    //Constructor
    public Staff(String name, Server server){
        setName(name);
        setStatus(StaffStatus.IDLE);
        this.server = server;
        this.dishStock = server.getDishStock();
        this.ingredientsStock = server.getIngredientsStock();
    }

    @Override
    public void run() {

        while (true) { //forever, for the works are slaves and get not

            try {
                //checks every 10th of a second
                //This avoid strain on the computer
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Dish dish = dishStock.takeFromRestockQueue();
            if (dish != null) {
                boolean dishMade = false;
                Map<Ingredient, Number> recipe = new HashMap<>(dish.getRecipe()); //what is needed to be collected
                Map<Ingredient, Number> obtained = new HashMap<>(dish.getRecipe()); //what has been collected
                for(Ingredient ingredient : obtained.keySet()){                     //sets all values to 0
                    obtained.put(ingredient, 0);
                }
                while (!dishMade) {
                    //for every ingredient needed, try to take this from the stock and store if for later use
                    for(Ingredient ingredient : obtained.keySet()){
                        if(recipe.get(ingredient).intValue() > obtained.get(ingredient).intValue()){
                            if (ingredientsStock.takeStock(ingredient)){
                                obtained.put(ingredient, obtained.get(ingredient).intValue()+1);
                            }
                        }
                    }
                    //when the recipe matches the ingredients that have been obtained, cook the dish
                    if(recipe.equals(obtained)){
                        setStatus(StaffStatus.COOKING);
                        getCookin(dish);
                        dishMade = true;
                    }
                }
            }
        }
    }

    /**
     * Cooks the dish and adds it to dish stock
     * @param dish
     */
    public void getCookin(Dish dish) {
        Random rand = new Random();
        try {
            System.out.println(getName() + ": cooking");
            Thread.sleep(3000); //(rand.nextInt(40)+20)*1000); // Simulates the time to prepare
            dishStock.addStock(dish, dish.getRestockAmount());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + ": finished");
       setStatus(StaffStatus.IDLE);
    }

    /**
     * Updates the status of the staff's job
     * @param status aka current job
     */
    public void setStatus(StaffStatus status) {
        this.status = status;
        notifyUpdate();
    }

    //Getters and setters
    @Override
    public String getName() {
        return name;
    }

    public String getStatus(){
        return status.toString();
    }
}