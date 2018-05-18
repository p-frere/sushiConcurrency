package server;

import common.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * This class is populated then serialized to store a current state of the system
 * The class is then deserialized when information needs to be recovered
 */
public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    //collection of all the information needed
    private List<Drone> drones;
    private List<Staff> staffs;
    private List<Order> orders;
    private List<Postcode> postcodes;
    private List<User> users;
    private List<Supplier> suppliers;
    private List<Dish> dishes;
    private List<Ingredient> ingredients;

    private Queue<Order> incomingOrders;
    private Queue<Order> outgoingOrders;
    private Map<Dish, Number> dishStock;
    private Set<Dish> dishRestock;
    private Map<Ingredient, Number> ingredientStock;
    private Set<Ingredient> ingredientRestock;

    //Constructor
    public SaveData(List<Drone> drones, List<Staff> staffs, List<Order> orders, List<Postcode> postcodes, List<User> users,
                    List<Supplier> suppliers, List<Dish> dishes, List<Ingredient> ingredients, Queue<Order> incomingOrders,
                    Queue<Order> outgoingOrders, Map<Dish, Number> dishStock, Set<Dish> dishRestock,
                    Map<Ingredient, Number> ingredientStock, Set<Ingredient> ingredientRestock){
        this.drones = drones;
        this.staffs = staffs;
        this.orders = orders;
        this.postcodes = postcodes;
        this.users = users;
        this.suppliers = suppliers;
        this.dishes = dishes;
        this.ingredients = ingredients;
        this.incomingOrders = incomingOrders;
        this.outgoingOrders = outgoingOrders;
        this.dishStock = dishStock;
        this.dishRestock = dishRestock;
        this.ingredientStock = ingredientStock;
        this.ingredientRestock = ingredientRestock;
    }

    //gettas and settas
    public Queue<Order> getIncomingOrders() {
        return incomingOrders;
    }

    public Queue<Order> getOutgoingOrders() {
        return outgoingOrders;
    }

    public Map<Dish, Number> getDishStock() {
        return dishStock;
    }

    public Set<Dish> getDishRestock() {
        return dishRestock;
    }

    public Map<Ingredient, Number> getIngredientStock() {
        return ingredientStock;
    }

    public Set<Ingredient> getIngredientRestock() {
        return ingredientRestock;
    }

    public List<Drone> getDrones() {
        return drones;
    }

    public List<Staff> getStaffs() {
        return staffs;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

}
