package common;

import java.io.Serializable;

/**
 * Models a supplier where ingredients are delivered from.
 * Each supplier has a distance from the sushi restaurant.
 */
public class Supplier extends Model implements Serializable {
    private Integer distance;

    //Constructor
    public Supplier(String name, Integer distance) {
        setName(name);
        this.distance = distance;
    }

    //Gets
    public Integer getDistance() {
        return distance;
    }

    @Override
    public String getName() {
        return name;
    }
}
