package common;

import java.util.ArrayList;

public class User {
    private String name;
    private String password;
    private String address;
    private String postCode;
    public ArrayList<Order> orders = new ArrayList<Order>();

    public User(String userName, String password, String address, String postCode)
    {
        this.name = userName;
        this.password = password;
        this.address = address;
        this.postCode = postCode;
    }

    //settos and gettos
    public String getUserName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getAddress(){
        return address;
    }

    public String getName() {
        return name;
    }

}

/*

POSTCODE:SO17 1BJ:2
USER:Admin:password:University Road:SO17 1BJ

 */