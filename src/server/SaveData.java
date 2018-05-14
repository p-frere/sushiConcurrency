package server;

import common.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Drone> drones;
    private List<Staff> staffs;
    private List<Order> orders;
    private List<Postcode> postcodes;
    private List<User> users;
    private List<Supplier> suppliers;
    private List<Dish> dishes;
    private List<Ingredient> ingredients;

    private Queue<Order> incomingOrders;    //orders that need (need to produced)
    private Queue<Order> outgoingOrders;
    private Map<Dish, Number> dishStock;
    private Set<Dish> dishRestock;
    private Map<Ingredient, Number> ingredientStock;
    private Set<Ingredient> ingredientRestock;

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

    public void setIncomingOrders(Queue<Order> incomingOrders) {
        this.incomingOrders = incomingOrders;
    }

    public Queue<Order> getOutgoingOrders() {
        return outgoingOrders;
    }

    public void setOutgoingOrders(Queue<Order> outgoingOrders) {
        this.outgoingOrders = outgoingOrders;
    }

    public Map<Dish, Number> getDishStock() {
        return dishStock;
    }

    public void setDishStock(Map<Dish, Number> dishStock) {
        this.dishStock = dishStock;
    }

    public Set<Dish> getDishRestock() {
        return dishRestock;
    }

    public void setDishRestock(Set<Dish> dishRestock) {
        this.dishRestock = dishRestock;
    }

    public Map<Ingredient, Number> getIngredientStock() {
        return ingredientStock;
    }

    public void setIngredientStock(Map<Ingredient, Number> ingredientStock) {
        this.ingredientStock = ingredientStock;
    }

    public Set<Ingredient> getIngredientRestock() {
        return ingredientRestock;
    }

    public void setIngredientRestock(Set<Ingredient> ingredientRestock) {
        this.ingredientRestock = ingredientRestock;
    }

    public List<Drone> getDrones() {
        return drones;
    }

    public void setDrones(List<Drone> drones) {
        this.drones = drones;
    }

    public List<Staff> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Staff> staffs) {
        this.staffs = staffs;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    public void setPostcodes(List<Postcode> postcodes) {
        this.postcodes = postcodes;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
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

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
