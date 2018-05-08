package server;


import common.Dish;
import common.Ingredient;
import common.Order;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    private Server server;

    public Config(Server server){
        this.server = server;

    }

    public void readIn(String file){
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] content = line.split(":");
                //System.out.println(content[0]);
                processLine(content);
            }
            br.close();
        } catch (Exception e){
            System.err.println(e.getMessage()); // handle exception

        }
    }

    public void processLine(String[] words){
        switch (words[0]){
            case "SUPPLIER":
                server.addSupplier(words[1], Integer.valueOf(words[2]));
                break;

            case "INGREDIENT":
                server.addIngredient(words[1], words[2], server.getSupplier(words[3]), Integer.valueOf(words[4]), Integer.valueOf(words[5]));
                //server.addDish(words[1], "It's just "+words[1],5, 1, 10);
                break;

            case "DISH":
                Dish dish = server.addDish(words[1], words[2], Integer.valueOf(words[3]), Integer.valueOf(words[4]), Integer.valueOf(words[5]));

                String[] ingrdientxQuantity = words[6].split(",");
                Map<Ingredient, Number> recipe = new HashMap<>();
                for(String item : ingrdientxQuantity){
                    String[] splitItems = item.split(" \\* ");
                    recipe.put(server.getIngredient(splitItems[1]), Integer.valueOf(splitItems[0]));
                }
                dish.setRecipe(recipe);
                break;

            case "POSTCODE":
                server.addPostcode(words[1], Integer.valueOf(words[2]));
                break;

            case "USER":
                server.addUser(words[1], words[2], words[3], server.getPostcode(words[4]));
                break;

            case "ORDER":
                String[] dishxQuantity = words[2].split(",");
                Map<Dish, Number> basket = new HashMap<>();
                for(String item : dishxQuantity){
                    String[] splitItems = item.split(" \\* ");
                    basket.put(server.getDish(splitItems[1]), Integer.valueOf(splitItems[0]));
                }
                server.addOrder(new Order(server.getUSer(words[1]),basket));
                break;

            case "STOCK":
                //System.out.println(words[1]);
                if(server.getDish(words[1]) != null)
                    server.setStock(server.getDish(words[1]), Integer.valueOf(words[2]));
                else
                    server.setStock(server.getIngredient(words[1]), Integer.valueOf(words[2]));
                break;

            case "STAFF":
                server.addStaff(words[1]);
                break;

            case "DRONE":
                server.addDrone(Integer.valueOf(words[1]));
                break;

            default:
                //System.out.println("line no recognized");
                break;
        }
    }
}
