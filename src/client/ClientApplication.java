package client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {
    Socket socket;
    Client client;


    public static void main(String[] args) throws IOException {

        System.out.println("Client Started");

        ClientApplication clientApp = new ClientApplication();
        //clientApp.initSocket();
        clientApp.initGraphics();

    }

    // Client Setup
    public void initSocket() throws IOException {
        try {
            socket = new Socket("127.0.0.1", 1342);
        } catch (IOException e) {
            System.out.println("socket delceration error");
            e.printStackTrace();
        }

        // accepting something from the server
        Scanner serverScanner = new Scanner(socket.getInputStream());
        String temp;

        // prints input stream to chat
        while (true) {
            temp = serverScanner.nextLine();
            System.out.println(temp);
        }
    }

    public void initGraphics(){
        client = new Client();
    }

    // sends submitted text to server
    public void Send() {
        //String text = read();

        // passes to the server with the username
        PrintStream ps;
        try {
            ps = new PrintStream(socket.getOutputStream());
            ps.println("stuff to send");

        } catch (IOException e) {
            System.out.println("error in print stream");
            e.printStackTrace();
        }
    }
}
