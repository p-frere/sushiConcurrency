package server;

import common.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server implements ServerInterface {
    boolean restockingIngredientsEnabled;
    boolean restockingDishesEnabled;
    DishStock dishStock;
    IngredientsStock ingredientsStock;
    List<Drone> drones;
    List<Staff> staffs;
    List<Order> orders;
    List<Postcode> postcodes;
    List<User> users;
    List<Supplier> suppliers;

    public Server(){
        restockingIngredientsEnabled = true;
        restockingDishesEnabled = true;
        drones = new ArrayList<>();
        staffs = new ArrayList<>();
        orders = new ArrayList<>();
        postcodes = new ArrayList<>();
        users = new ArrayList<>();
        suppliers = new ArrayList<>();
    }

    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        //todo
    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
        restockingIngredientsEnabled = enabled;
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        restockingDishesEnabled = enabled;
    }

    @Override
    public void setStock(Dish dish, Number stock) {
        dishStock.addStock(dish,(Integer) stock);
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        ingredientsStock.addStock(ingredient, (Integer) stock);
    }

    @Override
    public List<Dish> getDishes() {
        return dishStock.getDishes();
    }

    @Override
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        Dish dish = new Dish(name, description, (double)price, (Integer)restockThreshold, (Integer)restockAmount);
        dishStock.addStock(dish, 1);
        return dish;
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        dishStock.removeStock(dish);
    }

    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
        dish.addIngredient(ingredient, (Integer)quantity);
    }

    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
        dish.removeIngredient(ingredient);
    }

    @Override
    public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
        dish.setRecipe(recipe);
    }

    @Override
    public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
        dish.setRestock((Integer)restockThreshold, (Integer)restockAmount);
    }

    @Override
    public Number getRestockThreshold(Dish dish) {
        return dish.getRestockThreshold();
    }

    @Override
    public Number getRestockAmount(Dish dish) {
        return dish.getRestockAmount();
    }

    @Override
    public Map<Ingredient, Number> getRecipe(Dish dish) {
        return dish.getRecipe();
    }

    @Override
    public Map<Dish, Number> getDishStockLevels() {
        return dishStock.getStockLevels();
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ingredientsStock.getIngredients();
    }

    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold, Number restockAmount) {
        Ingredient ingredient = new Ingredient(name, unit, supplier, (Integer)restockThreshold, (Integer)restockAmount);
        ingredientsStock.addStock(ingredient, 0);
        return ingredient;
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
        ingredientsStock.removeStock(ingredient);
    }

    @Override
    public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
        ingredient.setRestock((Integer)restockThreshold, (Integer)restockAmount);
    }

    @Override
    public Number getRestockThreshold(Ingredient ingredient) {
        return ingredient.getRestockThreshold();
    }

    @Override
    public Number getRestockAmount(Ingredient ingredient) {
        return ingredient.getRestockAmount();
    }

    @Override
    public Map<Ingredient, Number> getIngredientStockLevels() {
        return ingredientsStock.getStockLevels();
    }

    @Override
    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier supplier = new Supplier(name, (Double) distance);
        suppliers.add(supplier);
        return supplier;
    }

    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        suppliers.remove(supplier);
    }

    @Override
    public Number getSupplierDistance(Supplier supplier) {
        return supplier.getDistance();
    }

    @Override
    public List<Drone> getDrones() {
        return drones;
    }

    @Override
    public Drone addDrone(Number speed) {
        Drone drone = new Drone((Integer)speed);
        drones.add(drone);
        return drone;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        drones.remove(drone);
        //will this actully work?
        //do we need to specifiy speed?
        //is it a genral drone
    }

    @Override
    public Number getDroneSpeed(Drone drone) {
        return drone.getSpeed();
    }

    @Override
    public String getDroneStatus(Drone drone) {
        return drone.getStatus().toString();
    }

    @Override
    public List<Staff> getStaff() {
        return staffs;
    }

    @Override
    public Staff addStaff(String name) {
        Staff staff = new Staff(name);
        staffs.add(staff);
        return staff;
    }

    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
        staffs.remove(staff);
        //will this work?
        //like drones
    }

    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
        orders.remove(order);
    }

    @Override
    public Number getOrderDistance(Order order) {
        return order.getOrderDistance();
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return (order.getStatus() == OrderStatus.COMPLETE);
    }

    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus().toString();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getOrderCost();
    }

    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    @Override
    public void addPostcode(String code, Number distance) {
        postcodes.add(new Postcode(code, (Double) distance));
    }

    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        postcodes.remove(postcode);
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void removeUser(User user) throws UnableToDeleteException {
        users.remove(user);
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        //todo
    }

    @Override
    public void notifyUpdate() {
        //todo
    }
}
