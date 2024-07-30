import java.util.Scanner;

public class Repl {

    public Client client;

    public Repl(String url) {
        client = new Client(url);
    }

    public void run() {
        System.out.println("Starting REPL");

        Scanner scanner = new Scanner(System.in);
        System.out.print(">>> ");
        String input = scanner.nextLine();
        while (!input.equals("quit")) {

            System.out.println(client.eval(input));

            System.out.print(">>> ");
            input = scanner.nextLine();
        }

    }

}
