package server;

import common.Dish;
import common.Ingredient;
import common.Order;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * The config file handles the opening and reading
 * on a config file in the correct format.
 * It then sets up the system to align with these passed settings
 */
public class Config {
    private Server server;

    //Constructor
    public Config(Server server){
        this.server = server;

    }

    /**
     * Controls the reading of a file,
     * process the content one line at a time
     * @param file location of the config file
     */
    public void readIn(String file){
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                //Creates an array of tokens in the line
                String[] content = line.split(":");
                processLine(content);
            }
            br.close();
        } catch (Exception e){
            System.err.println(e.getMessage());

        }
    }

    /**
     * Processes each array of words depending on the first word,
     * Assigns the following information to the correct place in the system
     * @param words array of tokens
     */
    public void processLine(String[] words){
        switch (words[0]){
            case "SUPPLIER":
                server.addSupplier(words[1], Integer.valueOf(words[2]));
                break;

            case "INGREDIENT":
                server.addIngredient(words[1], words[2], server.getSupplier(words[3]), Integer.valueOf(words[4]), Integer.valueOf(words[5]));
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
                String[] dishxQuantity = words[2].split(",");  //splits on commas and astrix
                Map<Dish, Number> basket = new HashMap<>();
                for(String item : dishxQuantity){
                    String[] splitItems = item.split(" \\* ");
                    basket.put(server.getDish(splitItems[1]), Integer.valueOf(splitItems[0]));
                }
                server.addOrder(new Order(server.getUSer(words[1]),basket));
                break;

            case "STOCK":
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
                break;
        }
    }
}
