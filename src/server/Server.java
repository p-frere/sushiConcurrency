package server;

import common.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements ServerInterface {
    private boolean restockingIngredientsEnabled;
    private boolean restockingDishesEnabled;

    //List of all available resources
    //Do not represent actual items
    //More like an index
    private List<Drone> drones;
    private List<Staff> staffs;
    private List<Order> orders;
    private List<Postcode> postcodes;
    private List<User> users;
    private List<Supplier> suppliers;
    private List<Dish> dishes;
    private List<Ingredient> ingredients;

    private Authenticate authenticate;
    private Config config;
    private DishStock dishStock;
    private IngredientsStock ingredientsStock;
    private OrderManager orderManager;
    private Update update;

    public static final int PORT = 4444;
    private List<ServerComms> userThreads;

    public Server(){
        //init
        restockingIngredientsEnabled = true;
        restockingDishesEnabled = true;
        drones = new ArrayList<>();
        staffs = new ArrayList<>();
        orders = new ArrayList<>();
        postcodes = new ArrayList<>();
        users = new ArrayList<>();
        suppliers = new ArrayList<>();
        dishes = new ArrayList<>();
        ingredients = new ArrayList<>();

        //set up relations to classes
        authenticate = new Authenticate(this);
        config = new Config(this);

        dishStock = new DishStock(this);
        ingredientsStock = new IngredientsStock(this);
        orderManager = new OrderManager(this);


        //import settings for testing
        //todo remove later
        try {
            loadConfiguration("src/config.txt");
        }catch (Exception e){
            System.err.println(e);
        }

        //server setup
        userThreads = new ArrayList<>();
        initSocket();

        Thread isThrd = new Thread(ingredientsStock);
        isThrd.start();

        for (Drone drone : drones){
            new Thread(drone).start();
        }
        Thread dsThrd = new Thread(dishStock);
        dsThrd.start();

        for (Staff staff : staffs){
            new Thread(staff).start();
        }

        Thread omThrd = new Thread(orderManager);
        omThrd.start();

        new Thread(new OrderBuilder(this)).start();


    }


    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        config.readIn(filename);
        update = new Update(dishes, postcodes);
    }

    public Update getUpdate() {
        return update;
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

    //-----------------Communication--------------------------------
    public void initSocket() {
        new Thread(new Comms(this)).start();
    }

//    //sends to all users
//    public void sendToAll(Payload payload) {
//        System.out.println("send to all");
//        //for all threads, send the message back
//        for(ServerComms st : userThreads){
//            try {
//                st.sendMessage(payload);
//            } catch (IOException e) {
//                System.out.println("can't relay info to all threads");
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public void sendToUser(Integer id, Payload payload) {
//        System.out.println("send to user");
//            try {
//                userThreads.get(id).sendMessage(payload);
//            } catch (IOException e) {
//                System.out.println("can't relay info to thread");
//                e.printStackTrace();
//            }
//    }

    public void addUserThread(ServerComms sc){
        userThreads.add(sc);
    }

    public void removeUserThread(String username){
        userThreads.remove(username);
    }

    //---------------------Dish--------------------------
    @Override
    public List<Dish> getDishes() {
        return dishStock.getDishes();
    }

    public Dish getDish(String name){
        for(Dish dish : dishes){
            if (dish.getName().equals(name)){
                return dish;
            }
        }
        System.out.println("dish requested doesn't exist");
        return null;
    }

    @Override
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        Dish dish = new Dish(name, description, price.intValue(), restockThreshold.intValue(), restockAmount.intValue());
        dish.setRecipe(new HashMap<>());
        dishStock.addStock(dish, 0);
        dishes.add(dish);
        return dish;
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        dishStock.removeStock(dish);
        dishes.remove(dish);
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

    //-----------------Ingredient-------------------------------
    @Override
    public List<Ingredient> getIngredients() {
        return ingredientsStock.getIngredients();
    }

    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold, Number restockAmount) {
        Ingredient ingredient = new Ingredient(name, unit, supplier, (Integer)restockThreshold, (Integer)restockAmount);
        ingredientsStock.addStock(ingredient, 0);
        ingredients.add(ingredient);
        return ingredient;
    }

    public Ingredient getIngredient(String ingredientName){
        for(Ingredient item : ingredients){
            if(item.getName().equals(ingredientName)){
                return item;
            }
        }
        System.out.println("Ingredient needed doesn't exist");
        return null;
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
        ingredientsStock.removeStock(ingredient);
        ingredients.remove(ingredient);
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

    //---------------Supplier-------------------------------
    @Override
    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public Supplier getSupplier(String name){
        for(Supplier supplier : suppliers){
            if (supplier.getName().equals(name)){
                return supplier;
            }
        }
        System.out.println("supplier requested doesn't exist");
        return null;
    }

    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier supplier = new Supplier(name, distance.intValue());
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

    //--------------Drone-------------------
    @Override
    public List<Drone> getDrones() {
        return drones;
    }

    @Override
    public Drone addDrone(Number speed) {
        Drone drone = new Drone((Integer)speed, this);
        drones.add(drone);
        return drone;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        drones.remove(drone);
    }

    @Override
    public Number getDroneSpeed(Drone drone) {
        return drone.getSpeed();
    }

    @Override
    public String getDroneStatus(Drone drone) {
        return drone.getStatus().toString();
    }

    //---------Staff--------------------
    @Override
    public List<Staff> getStaff() {
        return staffs;
    }

    @Override
    public Staff addStaff(String name) {
        Staff staff = new Staff(name, this);
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

    //-------------Order--------------------
    @Override
    public List<Order> getOrders() {
        return orders;

    }

    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
        orders.remove(order);   //removes from servers persistant list
    }

    public void addOrder(Order order){
        orders.add(order);
        orderManager.addOrder(order);
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

    public void cancelOrder(Order order){
        orderManager.cancelOrder(order);
        try {
            removeOrder(order);
        } catch (UnableToDeleteException e) {
            e.printStackTrace();
        }
    }

    //-------------Postcode--------------------------
    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    @Override
    public void addPostcode(String code, Number distance) {
        postcodes.add(new Postcode(code, (Integer) distance));
    }

    public Postcode getPostcode(String code){
        for (Postcode postcode : postcodes){
            if(postcode.getPostcode().equals(code)){
                return postcode;
            }
        }
        System.out.println("postcode not found");
        return null;
    }

    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        postcodes.remove(postcode);
    }

    //-------------Users------------------------------

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void removeUser(User user) throws UnableToDeleteException {
        users.remove(user);
        authenticate.removeUser(user.getName());
    }

    public void addUser(String userName, String password, String address, Postcode postCode){
        User user = new User(userName, password,address, postCode);
        users.add(user);
        authenticate.register(userName, user);
    }

    public User login(User user){
        return authenticate.login(user.getUserName(), user.getPassword());
    }

    public User register(User user){
        return authenticate.register(user.getUserName(), user);
    }

    public User getUSer(String username){
        for (User user : users){
            if(user.getName().equals(username)){
                return user;
            }
        }
        System.out.println("postcode not found");
        return null;
    }

    //---------Stocks and Managers----------------------

    public DishStock getDishStock() {
       return dishStock;
    }

    public IngredientsStock getIngredientsStock() {
        return ingredientsStock;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        //listners
    }

    @Override
    public void notifyUpdate() {
        //todo
    }
}
