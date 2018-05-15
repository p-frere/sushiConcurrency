package server;

import common.*;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The server coordinates and allows communication between all classes
 */
public class Server implements ServerInterface {
    private boolean restockingIngredientsEnabled;
    private boolean restockingDishesEnabled;

    //List of all available resources
    //Do not represent actual items, more like an index
    private List<Drone> drones;
    private List<Staff> staffs;
    private List<Order> allOrders;
    private List<Postcode> postcodes;
    private List<User> users;
    private List<Supplier> suppliers;


    private Authenticate authenticate;
    private Config config;
    private DishStock dishStock;
    private IngredientsStock ingredientsStock;
    private OrderManager orderManager;
    private Update update;
    private Storage storage;
    private boolean setUpComplete;

    public static final int PORT = 4444;
    private List<ServerComms> userThreads;

    public Server(){
        setUpComplete = false;
        //init
        restockingIngredientsEnabled = true;
        restockingDishesEnabled = true;
        drones = new ArrayList<>();
        staffs = new ArrayList<>();
        allOrders = new ArrayList<>();
        postcodes = new ArrayList<>();
        users = new ArrayList<>();
        suppliers = new ArrayList<>();

        dishStock = new DishStock(this);
        ingredientsStock = new IngredientsStock(this);
        orderManager = new OrderManager(this);

        //set up relations to classes
        authenticate = new Authenticate(this);
        config = new Config(this);
        storage = new Storage(this);

        storage.recover();

        //import settings for testing
        //todo remove later
//        try {
//            loadConfiguration("src/config.txt");
//        }catch (Exception e){
//            System.err.println(e);
//        }

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

        setUpComplete = true;
        //storage.save();

    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
        restockingIngredientsEnabled = enabled;
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        restockingDishesEnabled = enabled;
    }


    //---------------File Saves-------------------------------------
    public void save(){
        storage.save();
    }

    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        clearData();
        config.readIn(filename);
    }

    /**
     * Removes all data from the server
     * A fresh start
     */
    public void clearData(){
        users.clear();
        drones.clear();
        staffs.clear();
        postcodes.clear();
        suppliers.clear();

        orderManager.setIncomingOrders(new ConcurrentLinkedQueue<>());
        orderManager.setOutgoingOrders(new ConcurrentLinkedQueue<>());
        allOrders.clear();
        dishStock.setStock(new ConcurrentHashMap<>());
        dishStock.setRestock(new HashSet<>());
        ingredientsStock.setStock(new ConcurrentHashMap<>());
        ingredientsStock.setRestock(new HashSet<>());


    }

    //-----------------Communication--------------------------------
    public void initSocket() {
        new Thread(new Comms(this)).start();
    }

    //sends to all users
    public void sendToAll(Payload payload) {
        System.out.println("send to all");
        //for all threads, send the message back
        for(ServerComms st : userThreads){
            st.sendMessage(payload);
        }

    }

    public void sendToUser(User user, Payload payload) {
        System.out.println("send to user");
        if(user.getThreadID() == null){
            System.out.println("WARNING: null threadID, cannot be sent");
            return;
        }
        userThreads.get(user.getThreadID()).sendMessage(payload);
    }

    public Update getUpdate() {
        return new Update(getDishes(), postcodes);
    }

    public Integer addUserThread(ServerComms sc){
        userThreads.add(sc);
        return userThreads.indexOf(sc);
    }

    /**
     * Removes a the thread assigned to the user when they leave
     * @param sc
     */
    public void removeUserThread(ServerComms sc){
        userThreads.remove(sc);
    }

    /**
     * Gets the thread ID, a number assigned to the thread so it can be
     * referenced when sending messages.
     * @param sc Server Comms
     * @return Integer ID
     */
    public Integer getID(ServerComms sc){
        return userThreads.indexOf(sc);
    }

    //---------------------Dish--------------------------
    @Override
    public List<Dish> getDishes() {
        return dishStock.getDishes();
    }

    public Dish getDish(String name){
        for(Dish dish : getDishes()){
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

        //sends the updated dish menu to the client
        if (setUpComplete)
            sendToAll(new Payload(getUpdate(), TransactionType.updateInfo));
        return dish;
    }

    public Dish addDish(Dish dish) {
        Map<Ingredient,Number> newRecipe = new HashMap<>();
        for(Ingredient ingredient : dish.getRecipe().keySet()){
            newRecipe.put(getIngredient(ingredient.getName()), dish.geetIngredientAmount(ingredient));
        }
        dish.setRecipe(newRecipe);
        dishStock.addStock(dish, 0);
        return dish;
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        dishStock.removeStock(dish);

        //sends the updated dish menu to the client
        if (setUpComplete)
            sendToAll(new Payload(getUpdate(), TransactionType.updateInfo));
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
        return dishStock.getStock();
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
        return ingredient;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredient.setSupplier(getSupplier(ingredient.getSupplier().getName()));
        ingredientsStock.addStock(ingredient, 0);
    }

    public Ingredient getIngredient(String ingredientName){
        for(Ingredient item : ingredientsStock.getIngredients()){
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

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
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
        return allOrders;
    }

    @Override
    public void removeOrder(Order order) {
        order = getOrder(order);
        orderManager.cancelOrder(order);  //removes from servers persistant list
        allOrders.remove(order);
    }

    public void addOrder(Order order){
        Map<Dish, Number> newBasket = new HashMap<>();
        for (Dish dish : order.getBasket().keySet()){
            newBasket.put(getDish(dish.getName()), order.geetDishAmount(dish));
        }
        order.setBasket(newBasket);
        order.setUser(getUSer(order.getUser().getName()));
        allOrders.add(order);
        orderManager.addOrder(order);
    }

    public Order getOrder(Order newOrder){
        for (Order order : allOrders){
            if (order.getBasket().equals(newOrder)){
                return order;
            }
        }
        return null;
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

    //-------------Postcode--------------------------
    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    public void setPostcodes(List<Postcode> postcodes) {
        this.postcodes = postcodes;
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

    public void addUser(User user){
        users.add(user);
        authenticate.register(user.getUserName(), user);
        user.setThreadID(null);
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

    @Override
    public void setStock(Dish dish, Number stock) {
        dishStock.addStock(dish,(Integer) stock);
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        ingredientsStock.addStock(ingredient, (Integer) stock);
    }

    public DishStock getDishStock() {
       return dishStock;
    }

    public IngredientsStock getIngredientsStock() {
        return ingredientsStock;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    //---------------------------------------------------------------


    @Override
    public void addUpdateListener(UpdateListener listener) {
        //listners
    }

    @Override
    public void notifyUpdate() {
        //todo
    }
}
