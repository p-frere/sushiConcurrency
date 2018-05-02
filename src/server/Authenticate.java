package server;

import common.Postcode;
import common.User;

import java.util.HashMap;
//user postcode class?
//use hashcode
//write to file?

public class Authenticate {
    HashMap<String, User> users;
    public Authenticate(){
        users = new HashMap<>();
    }

    public User login(String username, String password){
        if(!checkExists(username))
            return null;
        else
            return users.get(username);
    }

    public boolean register(String username, String password, Postcode postcode, String address){
        if (!checkExists(username))
            return false;
        else {
            //new postcode?
            users.put(username, new User(username, password, address, postcode));
            return true;
        }

    }

    public boolean checkExists(String username){
        return users.containsKey(username);
    }
}
