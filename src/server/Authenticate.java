package server;

import common.User;
import java.util.HashMap;

/**
 * The Authenticate class handles login and registration
 * of users
 */
public class Authenticate {
    private HashMap<String, User> users; //current users info
    private Server server;

    //Constructor
    public Authenticate(Server server){
        users = new HashMap<>();
        this.server = server;
    }

    /**
     * Checks username and password credentials
     * returns a user if credentials are valid else it returns null
     * @param username users name
     * @param password users password
     * @return user
     */
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

    /**
     * Registers a user with a username and user object
     * This allows authentication later
     * @param username name of object
     * @param user object
     * @return
     */
    public User register(String username, User user ){
        if (checkExists(username))
            return null;
        else {
            users.put(username, user);
            server.addToUserList(user);
            return user;
        }
    }

    /**
     * Removes the ability for a user to be able to login
     * @param username name of user
     */
    public void removeUser(String username){
        users.remove(username);
    }

    /**
     * Checks if a username exists before searching for infomation
     * @param username
     * @return user or null if no user found
     */
    public boolean checkExists(String username){
        return users.containsKey(username);
    }

    //getters and setters
    public void setUsers(HashMap<String, User> users){
        this.users = users;
    }
}
