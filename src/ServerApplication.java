import server.Server;
import server.ServerInterface;
import server.ServerWindow;
import java.io.IOException;

public class ServerApplication {
    public static void main(String[] args) throws IOException {
        ServerApplication app = new ServerApplication();
        app.launchGUI(app.initialise());
    }

    private Server initialise(){
        return new Server();
    }

    private void launchGUI(ServerInterface serverInterface){
        ServerWindow sw = new ServerWindow(serverInterface);
    }
}
