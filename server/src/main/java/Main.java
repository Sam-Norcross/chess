import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(0);
        System.out.println("Server started!");
        System.out.println("http://localhost:" + port);

    }
}