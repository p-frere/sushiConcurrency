package server;

import common.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The server coordinates and allows communication between all classes
 * on the server side. It manages the running and organisation of them all
 * being the central class for the manufacturing of sushi
 */
public class Server implements ServerInterface {
    private boolean restockingIngredientsEnabled;
    private boolean restockingDishesEnabled;

    //List of all available resources
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
    private Storage storage;
    private boolean setUpComplete;
    private ArrayList<UpdateListener> listeners;

    public static final int PORT = 4444;
    private List<ServerComms> userThreads;

    //Constructor
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
        listeners = new ArrayList<>();

        dishStock = new DishStock(this);
        ingredientsStock = new IngredientsStock(this);
        orderManager = new OrderManager(this);

        //set up relations to classes
        authenticate = new Authenticate(this);
        config = new Config(this);
        storage = new Storage(this);

        //recovers last session from storage
        storage.recover();

        //server setup
        userThreads = new ArrayList<>();
        initSocket();

        //Threads started
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

    /**
     * Saves the current state of the information
     * stored in the system,
     * set to save after each order completion
     */
    public void save(){
        storage.save();
    }

    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        clearData();
        config.readIn(filename);
        if (setUpComplete) {
            sendToAll(new Payload(getUpdate(), TransactionType.updateInfo));
        }
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
        authenticate.setUsers(new HashMap<>());
    }

    //-----------------Communication--------------------------------

    /**
     * Creates a new Comms thread which searches for new clients
     */
    private void initSocket() {
        new Thread(new Comms(this)).start();
    }

    /**
     * Sends a payload out to all users
     * @param payload
     */
    public void sendToAll(Payload payload) {
        System.out.println("send to all");
        //for all threads, send the message back
        for(ServerComms st : userThreads){
            st.sendMessage(payload);
        }

    }

    /**
     * sends a payloud to one specific user
     * @param user user object
     * @param payload payload object
     */
    public void sendToUser(User user, Payload payload) {
        System.out.println("send to user");
        if(user.getThreadID() == null){
            System.out.println("WARNING: null threadID, cannot be sent");
            return;
        }
        userThreads.get(user.getThreadID()).sendMessage(payload);
    }

    /**
     * Calls the creation and returns an update object
     * which contains information to update the client
     * and can be sent to them
     * @return
     */
    public Update getUpdate() {
        return new Update(getDishes(), postcodes);
    }

    /**
     * Adds a new thread to communicate to a user with
     * @param sc ServerComms
     * @return the index of the thread
     */
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
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        Dish dish = new Dish(name, description, price.intValue(), restockThreshold.intValue(), restockAmount.intValue());
        dish.setRecipe(new HashMap<>());
        dishStock.addStock(dish, 0);
        notifyUpdate();

        //sends the updated dish menu to the client
        if (setUpComplete) {
            sendToAll(new Payload(getUpdate(), TransactionType.updateInfo));
        }
        return dish;
    }

    /**
     * Adds a Dish to the dish stock and makes the ingredients it uses
     * instances of objects used in the server
     * @param dish
     * @return
     */
    public Dish addDish(Dish dish) {
        Map<Ingredient,Number> newRecipe = new HashMap<>();
        for(Ingredient ingredient : dish.getRecipe().keySet()){
            newRecipe.put(getIngredient(ingredient.getName()), dish.geetIngredientAmount(ingredient));
        }
        dish.setRecipe(newRecipe);
        dishStock.addStock(dish, 0);
        notifyUpdate();
        return dish;
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        dishStock.removeStock(dish);
        notifyUpdate();
        //sends the updated dish menu to the client
        if (setUpComplete) {
            sendToAll(new Payload(getUpdate(), TransactionType.updateInfo));
        }
    }
    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
        dish.addIngredient(ingredient, (Integer)quantity);
    }

    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
        dish.removeIngredient(ingredient);
    }

    //getts and setts
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
        System.out.println("dish requested doesn't exist = " + name);
        return null;
    }

    //-----------------Ingredient-------------------------------
    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold, Number restockAmount) {
        Ingredient ingredient = new Ingredient(name, unit, supplier, (Integer)restockThreshold, (Integer)restockAmount);
        ingredientsStock.addStock(ingredient, 0);
        return ingredient;
    }

    /**
     * Adds an already existing ingredient and by getting it the
     * servers instance of supplier and adding it to stock
     * @param ingredient
     */
    public void addIngredient(Ingredient ingredient) {
        ingredient.setSupplier(getSupplier(ingredient.getSupplier().getName()));
        ingredientsStock.addStock(ingredient, 0);
        notifyUpdate();
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
        ingredientsStock.removeStock(ingredient);
        notifyUpdate();
    }

    //Gets and Sets
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
    public List<Ingredient> getIngredients() {
        return ingredientsStock.getIngredients();
    }

    public Ingredient getIngredient(String ingredientName){
        for(Ingredient item : ingredientsStock.getIngredients()){
            if(item.getName().equals(ingredientName)){
                return item;
            }
        }
        System.out.println("WARNING: Ingredient needed doesn't exist");
        return null;
    }


    //---------------Supplier-------------------------------
    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier supplier = new Supplier(name, distance.intValue());
        suppliers.add(supplier);
        notifyUpdate();
        return supplier;
    }

    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        suppliers.remove(supplier);
        notifyUpdate();
    }

    //Gets and Sets
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
        notifyUpdate();
        if(setUpComplete){
            new Thread(drone).start();
        }
        return drone;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        drones.remove(drone);
        notifyUpdate();
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
        notifyUpdate();
        if(setUpComplete){
            new Thread(staff).start();
        }
        return staff;
    }

    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
        staffs.remove(staff);
        notifyUpdate();
    }

    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    //-------------Order--------------------
    @Override
    public void removeOrder(Order order) {
        //order = getOrder(order);
        orderManager.removeOrder(order);  //removes from servers persistant list
        allOrders.remove(order);
        notifyUpdate();
    }

    /**
     * sets an order to canceled than attempts to remove it
     * @param order
     */
    public void cancelOrder(Order order) {
        order = getOrder(order);
        if(order != null) {
            order.setStatus(OrderStatus.CANCELED);
            removeOrder(order);
        }
    }

    /**
     * Adds and order to the list of orders
     * as well as adding it to the order manager.
     * The dishes in the basket must be reinitialized
     * To the ones the server uses
     * @param order order object
     */
    public void addOrder(Order order){
        Map<Dish, Number> newBasket = new HashMap<>();

        for (Dish dish : order.getBasket().keySet()){
            newBasket.put(getDish(dish.getName()), order.geetDishAmount(dish));
        }
        order.setBasket(newBasket);
        order.setUser(getUSer(order.getUser().getName()));
        order.getUser().addOrder(order, false);
        allOrders.add(order);
        orderManager.addOrder(order);

        notifyUpdate();
    }

    //getters and setters
    public Order getOrder(Order newOrder){
        for (Order order : allOrders){
            if(order.getOrderID() != null) {
                if (order.getOrderID().equals(newOrder.getOrderID())) {
                    return order;
                }
            }
        }
        return null;
    }

    @Override
    public List<Order> getOrders() {
        return allOrders;
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
    public void addPostcode(String code, Number distance) {
        postcodes.add(new Postcode(code, (Integer) distance));
        notifyUpdate();
    }

    /**
     * Returns the postcode object for a string code
     * @param code string of the postcode
     * @return postcode
     */
    public Postcode getPostcode(String code){
        for (Postcode postcode : postcodes){
            if(postcode.getPostcode().equals(code)){
                return postcode;
            }
        }
        return null;
    }

    /**
     * Removes a postcode from the stored list
     * @param postcode postcode to remove
     * @throws UnableToDeleteException
     */
    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        postcodes.remove(postcode);
        if (postcodes.contains(postcode)){
            throw new UnableToDeleteException("Unable to delete postcode");
        }
        notifyUpdate();
    }

    //Setters and getters
    @Override
    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    public void setPostcodes(List<Postcode> postcodes) {
        this.postcodes = postcodes;
    }

    //-------------Users------------------------------

    /**
     * Removes a user from the system
     * @param user to remove
     * @throws UnableToDeleteException
     */
    @Override
    public void removeUser(User user) throws UnableToDeleteException {
        users.remove(user);
        authenticate.removeUser(user.getName());
        if (users.contains(user)){
            throw new UnableToDeleteException("Unable to delete user");
        }
        notifyUpdate();
    }

    /**
     * Adds a new user to the server
     * @param userName
     * @param password
     * @param address
     * @param postCode
     */
    public void addUser(String userName, String password, String address, Postcode postCode){
        User user = new User(userName, password,address, postCode);
        authenticate.register(userName, user);
    }

    /**
     * Initialises an returning user object
     * @param user
     */
    public void addUser(User user){
        authenticate.register(user.getUserName(), user);
        user.setThreadID(null);
    }

    /**
     * Adds the user to a central list in the server
     * used for searching
     * @param user
     */
    public void addToUserList(User user){
        users.add(user);
        notifyUpdate();
    }

    /**
     * Requests the login authentication of a user
     * @param user
     * @return
     */
    public User login(User user){
        return authenticate.login(user.getUserName(), user.getPassword());
    }

    /**
     * Requests a registration of a user
     * @param user
     * @return
     */
    public User register(User user){
        return authenticate.register(user.getUserName(), user);
    }


    //Getters and Setters
    public User getUser(Integer ID){
        for(User user : users){
            if(user.getThreadID() == ID){
                return user;
            }
        }
        return null;
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

    @Override
    public List<User> getUsers() {
        return users;
    }


    //---------Stocks and Managers----------------------

    //getters and setters
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
            listeners.add(listener);
        }

        @Override
        public void notifyUpdate() {
            if (setUpComplete) {
                for(UpdateListener listener : listeners) {
                    listener.updated(new UpdateEvent());
                }
            }
        }
}
