package common;

public class Postcode extends Model {
    private String code;
    private Double distance;

    public Postcode(String code, Double distance){
        this.distance = distance;
        this.code = code;
    }

    public String getPostcode() {
        return code;
    }

    @Override
    public String getName() {
        return null;
    }

    public Double getDistance() {
        return distance;
    }
}
