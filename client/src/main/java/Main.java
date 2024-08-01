import client.Repl;
//import server.Server;

public class Main {
    public static void main(String[] args) {
//        Server server = new Server();
//        int port = server.run(0);
//        String url = "http://localhost:" + port;
//        System.out.println(url);
//
//        new Repl(url).run();
//        server.stop();

        String url = "http://localhost:8080";
        new Repl(url).run();

    }
}