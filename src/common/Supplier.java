package common;

import java.io.Serializable;

public class Supplier extends Model implements Serializable {
    private Integer distance;

    public Supplier(String name, Integer distance) {
        setName(name);
        this.distance = distance;
    }

    //Sets & Gets
    public Integer getDistance() {
        return distance;
    }

    @Override
    public String getName() {
        return name;
    }
}
