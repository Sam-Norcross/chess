import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(0);
        String url = "http://localhost:" + port;
        System.out.println(url);

        new Repl(url).run();    //TODO--circular dependency added here
        server.stop();
    }
}