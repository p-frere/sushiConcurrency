package common;

public class Postcode extends Model {
    private User user;
    private String postcode;

    public Postcode(User user, String postcode){
        this.user = user;
        this.postcode = postcode;
    }

    public String getPostcode() {
        return postcode;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return null;
    }
}
