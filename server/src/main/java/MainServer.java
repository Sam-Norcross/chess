//import client.Repl;
import server.Server;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080);
        String url = "http://localhost:" + port;
        System.out.println(url);

//        new Repl(url).run();    //TODO--circular dependency added here
//        server.stop();
    }
}