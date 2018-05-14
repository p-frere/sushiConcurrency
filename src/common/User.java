package common;

import java.io.Serializable;

/**
 * /**
 * A User is a customer to the sushi ordering service,
 * The data stored includes their login credentials, an address and previous orders
 */
public class User extends Model implements Serializable {
    private String name;
    private String password;
    private String address;
    private Postcode postCode;
    private Integer threadID;   //The threadID stores which server thread a user is assigned to when they connect to the server

    public User(String userName, String password, String address, Postcode postCode)
    {
        this.name = userName;
        this.password = password;
        this.address = address;
        this.postCode = postCode;
        threadID = null;
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

    public Integer getThreadID() {
        return threadID;
    }

    public void setThreadID(Integer threadID) {
        this.threadID = threadID;
    }
}

