package common;

public class Postcode extends Model {
    private String code;
    private Integer distance;

    public Postcode(String code, Integer distance){
        this.distance = distance;
        this.code = code;
    }

    public String getPostcode() {
        return code;
    }

    @Override
    public String getName() {
        return code;
    }

    public Integer getDistance() {
        return distance;
    }
}
