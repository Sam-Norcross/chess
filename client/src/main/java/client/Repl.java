package client;

import client.websocket.NotificationHandler;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler  {

    public Client client;

    public Repl(String url) {
        client = new Client(url, this);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        printPrompt();
        String input = scanner.nextLine();
        while (!input.equals("quit")) {

            System.out.println(client.eval(input));

            printPrompt();
            input = scanner.nextLine();
        }

    }

    @Override
    public void notify(NotificationMessage notification) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + notification.getMessage());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n>>> ");
    }

}
