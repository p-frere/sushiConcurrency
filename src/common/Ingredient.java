package common;

public class Ingredient extends Model {
    //model gives it name
    private String unit;
    private Supplier supplier;

    public Ingredient(String name, String unit, Supplier supplier){
        this.name = name;
        this.unit = unit;
        this.supplier = supplier;
    }


    //Setters and Getters
    @Override
    public String getName() {
        return null;
    }

    public String getUnit(){
        return unit;
    }

    public Supplier getSupplier(){
        return supplier;
    }
}
