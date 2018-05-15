package server;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import common.*;

/**
 * Represents a member of the kitchen staff
 * monitors the stock levels of dishes, should any fall below their
 * restocking levels and there are sufficient ingredients to
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
        status = StaffStatus.COOKING;
        Random rand = new Random();
        try {
            System.out.println(getName() + ": cooking");
            Thread.sleep((rand.nextInt(40)+20)*100); // Simulates the time to prepare
            dishStock.addStock(dish, dish.getRestockAmount());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + ": finished");
        status = StaffStatus.IDLE;
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