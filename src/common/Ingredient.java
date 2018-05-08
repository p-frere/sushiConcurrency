package common;

import java.io.Serializable;
import java.util.Map;

public class Ingredient extends Model implements Serializable{
    //model gives it name
    private String unit;
    private Supplier supplier;
    Integer restockThreshold;
    Integer restockAmount;

    public Ingredient(String name, String unit, Supplier supplier, Integer restockThreshold, Integer restockAmount){
        setName(name);
        this.unit = unit;
        this.supplier = supplier;
        this.restockAmount = restockAmount;
        this.restockThreshold = restockThreshold;
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
}
