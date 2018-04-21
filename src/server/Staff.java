package server;

public class Staff extends common.Model implements Runnable {
    private String currentJob;
    private boolean busy;

    @Override
    public void run() {
        while(!busy){
            //if restock needed and ther are ingrdientes
            //make dish
            //random time 20 -60 seconds
        }
    }

    @Override
    public String getName() {
        return null;
    }

    public void getStatus(){
        if (busy)
            System.out.println("I am doing " + currentJob);
        else
            System.out.println("I am Idel   ");
    }


}
