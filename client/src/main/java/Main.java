import client.Repl;
//import server.Server;

public class Main {
    public static void main(String[] args) {

        String url = "http://localhost:8080";
        new Repl(url).run();

    }
}