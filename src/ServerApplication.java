import server.Server;
import server.ServerInterface;
import server.ServerWindow;
import java.io.IOException;

public class ServerApplication {
    public static void main(String[] args) throws IOException {
        ServerApplication sa = new ServerApplication();
        sa.launchGUI(sa.initialise());
    }

    private Server initialise(){
        return new Server();
    }

    private void launchGUI(ServerInterface serverInterface){
        new ServerWindow(serverInterface);
    }
}
