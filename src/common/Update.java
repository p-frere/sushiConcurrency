package common;

import java.io.Serializable;
import java.util.List;

/**
 * Update is a class that holds a new menu and postcode selection
 * When these items are changed on the server they are bundled together in
 * this class and sent as an update to all clients
 */
public class Update extends Model implements Serializable{
    List<Dish> dishes;          //aka Menu
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
