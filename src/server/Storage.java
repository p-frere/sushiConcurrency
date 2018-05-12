package server;

import common.*;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class Storage implements Serializable, Runnable {
    private List<Drone> drones;
    private List<Staff> staffs;
    private List<Order> orders;
    private List<Postcode> postcodes;
    private List<User> users;
    private List<Supplier> suppliers;
    private List<Dish> dishes;
    private List<Ingredient> ingredients;

    Server server;
    String file = "src/data.ser";

    public Storage(Server server){
        this.server = server;
    }

    public void save(){
        drones = server.getDrones();
        staffs = server.getStaff();
        orders = server.getOrders();
        postcodes = server.getPostcodes();
        users = server.getUsers();
        suppliers = server.getSuppliers();
        dishes = server.getDishes();
        ingredients = server.getIngredients();

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(this);
            System.out.println("Saving Done");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void recover(){

    }


    @Override
    public void run() {

    }
}
