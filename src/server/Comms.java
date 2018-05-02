package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Comms extends Thread{
    Socket socket;
    ServerComms server;

    //Setup
    public Comms(Socket socket) {
        this.socket = socket;
        ServerComms.addUser(this);
    }

    //Accepts content
    public void receiveMessage() {
        String input;
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            try {
                while ((input = br.readLine()) != null) {
                    System.out.println(input);
                }
                socket.close();
            } catch (IOException e) {
                System.out.println("Server: User disconnected");
                e.printStackTrace();
            }
        } catch (IOException e1) {
            System.out.println("error in get input stream");
            e1.printStackTrace();
        }

    }

    //Sends a message
    public void sendMessage(String text) throws IOException {
        PrintStream printStream = new PrintStream(socket.getOutputStream());
        printStream.println(text);
    }

}

/*

Provide a 'sendMessage' method (or methods)
that allows each client to send a message object to the business application
and the business application to send a message to a specific client.

The applications will also need to check for incoming messages
by calling a 'receiveMessage' method (or methods) of the server.Comms class.

The types of these may vary depending on the types of message being sent/received.


If you prefer you may use Socket communication in the server.Comms
the business and client applications should be oblivious to the actual communication mechanism
used. That is, all I/O operations
must reside in the server.Comms class.
Access to them is only allowed via your
send/receive methods above.

 */
