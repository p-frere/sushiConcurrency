package common;

import java.io.Serializable;

/**
 * Models an ingredient used in a sushi dish
 * An ingredient has a name, a unit and a supplier
 */
public class Ingredient extends Model implements Serializable{
    //model gives it name
    private String unit;
    private Supplier supplier;
    private Integer restockThreshold;   //Restocks when the amount of an ingredient is below this threshold
    private Integer restockAmount;      //Amount to be restocked by
    private boolean fetching;           //marks if the ingredient is being operated on

    //constructor
    public Ingredient(String name, String unit, Supplier supplier, Integer restockThreshold, Integer restockAmount){
        setName(name);
        this.unit = unit;
        this.supplier = supplier;
        this.restockAmount = restockAmount;
        this.restockThreshold = restockThreshold;
        fetching = false;
    }

    //Setters and Getters
    @Override
    public String getName() {
        return name;
    }

    public String getUnit(){
        return unit;
    }

    public Supplier getSupplier(){
        return supplier;
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

    public boolean isFetching(){
        return fetching;
    }

    public void setFetching(boolean requested) {
        this.fetching = requested;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
