package common;

import java.util.Map;

public class Dish extends Model {
    private String description;
    private Double price;
    //model gives name
    public Map<Ingredient, Number> recipe;
    private Integer restockThreshold;
    private Integer restockAmount;

    public Dish(String name, String description, Double price, Integer restockThreshold, Integer restockAmount){
        this.name = name;
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
        //return  recipe.entrySet().iterator();
        return recipe;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
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
}
