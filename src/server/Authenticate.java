package server;

import common.User;
import java.util.HashMap;

/**
 * The Authenticate class handles login and registration
 * of users
 */
public class Authenticate {
    private HashMap<String, User> users;
    private Server server;

    public Authenticate(Server server){
        users = new HashMap<>();
        this.server = server;
    }

    public User login(String username, String password){
        if(!checkExists(username)) {
            System.out.println("doesn't exist");
            return null;
        } else {
            User currentUser = users.get(username);
            if (currentUser.getPassword().equals(password)){
                return currentUser;
            } else {
                return null;
            }
        }
    }

    public User register(String username, User user ){
        if (checkExists(username))
            return null;
        else {
            users.put(username, user);
            server.addToUserList(user);
            return user;
        }
    }

    public void setUsers(HashMap<String, User> users){
        this.users = users;
    }

    public void removeUser(String username){
        users.remove(username);
    }

    public boolean checkExists(String username){
        return users.containsKey(username);
    }
}
