package common;

import java.util.HashMap;
import java.util.Iterator;

public class Dish extends Model {
    private String description;
    private Double price;
    //model gives name
    public HashMap<Ingredient, Integer> ingredients;

    public Dish(String name, String description, Double price, HashMap<Ingredient, Integer> ingredients){
        this.ingredients = ingredients;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    //Getters and setters
    @Override
    public String getName() {
        return name;
    }

    public Iterator getRecipe(){
        return  ingredients.entrySet().iterator();
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }



}
