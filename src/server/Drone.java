package server;

import common.Dish;
import common.Ingredient;
import common.Model;
import common.User;

public class Drone extends Model implements Runnable {
    private Integer speed;

    @Override
    public String getName() {
        return null;
    }


    @Override
    public void run() {
        while (true) { //forever
            //if no dishes are in queue try again
            if () {
                dropOff();
            }
        }
    }


    //pick up items
    public void pickUp(Ingredient ingredient){
        try {
            Thread.sleep(ingredient.getSupplier().getDistance() / speed); //Milliseconds??
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //drop off dish to users
    public void dropOff(Dish dish, User user) { //user?
        try {
            Thread.sleep(2000); //how long?
        } catch (InterruptedException e) {

        }

    }

}
