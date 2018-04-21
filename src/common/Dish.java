package common;


import java.util.HashMap;

public class Dish extends Model {
    private String description;
    private Double price;
    private HashMap<Ingredient, Double> ingredient;

    @Override
    public String getName() {
        return null;
    }
}
