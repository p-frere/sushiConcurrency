package common;

import java.io.Serializable;
import java.util.List;

public class Update extends Model implements Serializable{
    List<Dish> dishes;
    List<Postcode> postcodes;

    public Update(List<Dish> dishes, List<Postcode> postcodes){
        this.dishes = dishes;
        this.postcodes = postcodes;
    }

    @Override
    public String getName() {
        return null;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public List<Postcode> getPostcodes() {
        return postcodes;
    }
}
