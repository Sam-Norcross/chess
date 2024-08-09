package client;

import client.websocket.NotificationHandler;
import ui.PrintUtils;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.PrintUtils.printPrompt;

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

            String[] tokens = input.split(" ");
            String command = tokens[0];
            if (!command.equals("observe") && !command.equals("join")) {
                printPrompt();
            }
            input = scanner.nextLine();
        }

    }

    @Override
    public void notify(NotificationMessage notification) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + notification.getMessage());
        printPrompt();
    }

    public void handleError(ErrorMessage errorMessage) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + errorMessage.getMessage() + RESET_TEXT_COLOR);
        printPrompt();
    }

}
