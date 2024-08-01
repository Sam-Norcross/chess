import chess.*;
import server.Server;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(0);
        String url = "http://localhost:" + port;
        System.out.println(url);

        new Repl(url).run();
        server.stop();

    }
}