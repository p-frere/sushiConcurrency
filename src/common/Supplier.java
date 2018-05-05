package common;

public class Supplier extends Model {
    private Integer distance;

    public Supplier(String name, Integer distance) {
        this.name = name;
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
