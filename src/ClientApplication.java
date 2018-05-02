
import client.Client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {
    Socket socket;
    Client client;


    public static void main(String[] args) throws IOException {
        Client client = new Client();
    }

    /*
    an initialise()
     method which takes no parameters and which prepares anything necessary for your implementation and
     returns the implementation of ClientInterface,

a launchGUI(clientInterface)
method which launches the GUI by creating a ClientWindow given the implementation of the ClientInterface,

a main() method which calls initialise and then launchGUI. It should not do anything else,
Running ClientApplication will launch the client.

     */
}
