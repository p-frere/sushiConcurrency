package server;

import common.Postcode;
import common.User;

import java.util.HashMap;
//user postcode class?
//use hashcode
//write to file?

public class Authenticate {
    private static HashMap<String, User> users;
    private Server server;

    public Authenticate(Server server){
        users = new HashMap<>();
        this.server = server;
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

    public boolean register(String username, User user ){
        if (!checkExists(username))
            return false;
        else {
            users.put(username, user);
            return true;
        }

    }

    public void removeUser(String username){
        users.remove(username);
    }

    public static boolean checkExists(String username){
        return users.containsKey(username);
    }
}
