package common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class User extends Model implements Serializable {
    private String name;
    private String password;
    private String address;
    private Postcode postCode;

    public User(String userName, String password, String address, Postcode postCode)
    {
        this.name = userName;
        this.password = password;
        this.address = address;
        this.postCode = postCode;
    }

    public User(String userName, String password)
    {
        this.name = userName;
        this.password = password;
    }

    //settos and gettos
    public String getUserName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Postcode getPostCode() {
        return postCode;
    }

    public String getAddress(){
        return address;
    }

    public String getName() {
        return name;
    }

}

