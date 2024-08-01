package client;

import java.util.Scanner;

public class Repl {

    public Client client;
    public final String prompt = "\n>>> ";

    public Repl(String url) {
        client = new Client(url);
    }

    public void run() {
        System.out.println("Starting REPL");

//        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);

        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        String input = scanner.nextLine();
        while (!input.equals("quit")) {

            System.out.println(client.eval(input));

            System.out.print(prompt);
            input = scanner.nextLine();
        }

    }

}
