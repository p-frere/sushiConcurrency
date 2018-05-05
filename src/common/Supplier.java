package common;

public class Supplier extends Model {
    private double distance;
    //items sold??

    public Supplier(String name, double distance) {
        this.setName(name);
        this.distance = distance;
    }

    //Sets & Gets
    public double getDistance() {
        return distance;
    }

    @Override
    public String getName() {
        return null;
    }
}
