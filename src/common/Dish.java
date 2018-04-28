package common;

import java.util.HashMap;
import java.util.Iterator;

public class Dish extends Model {
    private String description;
    private Double price;
    public HashMap<Ingredient, Double> ingredients;

    public Dish(){ //add ingredience things
        ingredients = new HashMap<>();
        //ingredient.add ...
    }

    @Override
    public String getName() {
        return null;
    }

    public Iterator getRecipe(){
        return  ingredients.entrySet().iterator();
    }

}
