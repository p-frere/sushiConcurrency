package common;

import java.io.Serializable;

/**
 * Contains a postcode for an area and distance to the area
 */
public class Postcode extends Model implements Serializable {
    private String code;
    private Integer distance;

    //Constructors
    public Postcode(String code, Integer distance){
        this.distance = distance;
        this.code = code;
        setName(code);
    }

    //Getters
    public String getPostcode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    public Integer getDistance() {
        return distance; //You can assume a fixed distance for each customer postcode?
    }
}
