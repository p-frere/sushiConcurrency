
import client.Client;
import client.ClientInterface;
import client.ClientWindow;
import java.io.IOException;

public class ClientApplication {

    public static void main(String[] args) throws IOException {
        ClientApplication ca = new ClientApplication();
        ca.launchGUI(ca.initialise());
    }

    private Client initialise(){
        return new Client();
    }

    private void launchGUI(ClientInterface clientInterface){
       new ClientWindow(clientInterface);
    }

}
