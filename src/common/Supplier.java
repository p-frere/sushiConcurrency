package common;

public class Supplier extends Model {
    private Integer distance;
    //items sold??

    public Supplier(String name, int distance) {
        this.setName(name);
        this.distance = distance;
    }

    //Sets & Gets
    public int getDistance() {
        return distance;
    }

    @Override
    public String getName() {
        return null;
    }
}
