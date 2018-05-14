package common;

import java.io.Serializable;
import java.util.Map;

/**
 * Models a Sushi dish that can be ordered
 * Each sushi dish has a name, a description, a price,
 * required ingredients and corresponding quantities (its recipe)
 */
public class Dish extends Model implements Serializable{
    private String description;
    private Integer price;
    public Map<Ingredient, Number> recipe;  //recipe
    private Integer restockThreshold;       //the amount of a dish before restocking is needed
    private Integer restockAmount;          //how much is restocked at a time
    private boolean fetching;               //marks if the dish is being operated on

    //constructor
    public Dish(String name, String description, Integer price, Integer restockThreshold, Integer restockAmount){
        setName(name);
        this.description = description;
        this.price = price;
        this.restockAmount = restockAmount;
        this.restockThreshold = restockThreshold;
    }

    //Getters and setters
    @Override
    public String getName() {
        return name;
    }

    public Map<Ingredient, Number> getRecipe(){
        return recipe;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPrice() {
        return price;
    }

    public void addIngredient(Ingredient ingredient, Integer amount){
        recipe.put(ingredient, amount);
    }

    public void removeIngredient(Ingredient ingredient){
        recipe.remove(ingredient);
    }

    public void setRecipe(Map<Ingredient, Number> map){
        recipe = map;
    }

    public Number geetIngredientAmount(Ingredient ingredient){
        return recipe.get(ingredient);
    }

    public void setRestock(Integer restockThreshold, Integer restockAmount){
        this.restockAmount = restockAmount;
        this.restockThreshold = restockThreshold;
    }

    public Integer getRestockThreshold() {
        return restockThreshold;
    }

    public Integer getRestockAmount() {
        return restockAmount;
    }

    public boolean isFetching() {
        return fetching;
    }

    public void setFetching(boolean requested) {
        this.fetching = requested;
    }
}
