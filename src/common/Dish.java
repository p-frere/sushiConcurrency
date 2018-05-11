package common;

import java.io.Serializable;
import java.util.Map;

public class Dish extends Model implements Serializable{
    private String description;
    private Integer price;
    //model gives name
    public Map<Ingredient, Number> recipe;
    private Integer restockThreshold;
    private Integer restockAmount;
    private boolean fetching;

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
