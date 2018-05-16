package server;

import common.*;
import java.io.*;

/**
 *
 */
public class Storage implements Serializable, Runnable {

    Server server;
    String file = "src/data.ser";
    OrderManager orderManager;
    IngredientsStock ingredientsStock;
    DishStock dishStock;

    public Storage(Server server){
        this.server = server;
        orderManager = server.getOrderManager();
        ingredientsStock = server.getIngredientsStock();
        dishStock = server.getDishStock();
    }

    public void save(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(orderManager.getIncomingOrders() == null);
        SaveData saveData = new SaveData(
                server.getDrones(),
                server.getStaff(),
                server.getOrders(),
                server.getPostcodes(),
                server.getUsers(),
                server.getSuppliers(),
                server.getDishes(),
                server.getIngredients(),
                orderManager.getIncomingOrders(),
                orderManager.getOutgoingOrders(),
                dishStock.getStock(),
                dishStock.getRestock(),
                ingredientsStock.getStock(),
                ingredientsStock.getRestock()
        );

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(saveData);
            System.out.println("Saving Done");
            outputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void recover(){
        try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))){
            SaveData data = (SaveData) inputStream.readObject();
            System.out.println("Read object");
            server.setSuppliers(data.getSuppliers());

            for(Ingredient ingredient : data.getIngredients()){
                server.addIngredient(ingredient);
            }

            for (Dish dish : data.getDishes()){
                server.addDish(dish);
            }

            server.setPostcodes(data.getPostcodes());

            for(User user : data.getUsers()){
                server.addUser(user);
            }

            for(Order order : data.getOrders()){
                server.addOrder(order);
            }

            for(Staff staff : data.getStaffs()){
                server.addStaff(staff.getName());
            }

            for(Drone drone : data.getDrones()){
                server.addDrone(drone.getSpeed());
            }
            System.out.println("loaded items");

            orderManager.setIncomingOrders(data.getIncomingOrders());
            orderManager.setOutgoingOrders(data.getOutgoingOrders());

            dishStock.setRestock(data.getDishRestock());
            dishStock.setStock(data.getDishStock());

            ingredientsStock.setRestock(data.getIngredientRestock());
            ingredientsStock.setStock(data.getIngredientStock());
            System.out.println("loaded data");

            //server.sendToAll(new Payload(server.getUpdate(), TransactionType.updateInfo));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
