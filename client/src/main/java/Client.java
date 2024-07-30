import model.UserData;
import server.ServerFacade;

public class Client {

    private final ServerFacade serverFacade;
    private boolean isLoggedIn;


    public Client(String url) {
        serverFacade = new ServerFacade(url);
        isLoggedIn = false;
    }

    public String eval(String input) {
        String message = "";

        String[] tokens = input.split(" ");
        String command = tokens[0];

        if (command.equals("help")) {
            return helpString();
        } else if (command.equals("login")) {
            try {
                serverFacade.login(new UserData(tokens[1], tokens[2], tokens[3]));
                isLoggedIn = true;
            } catch (Exception ex) {
                //
            }
        }
        if (command.equals("quit")) {
            return "quit";
        }
        return null;
    }

    private String helpString() {
        if (!isLoggedIn) {
            return """
                   Possible commands:
                    register <USERNAME> <PASSWORD> <EMAIL> --register a new user
                    login <USERNAME> <PASSWORD> --login an existing user
                    quit --exit the application
                    help --show possible commands
                   """;
        } else {
            return """
                   Possible commands:
                    create <NAME> --create a new game
                    list --list all games
                    join <ID> [WHITE | BLACK] --join a game
                    observe <ID> --observe a game
                    logout --log the current user out
                    quit --exit the application
                    help --show possible commands
                   """;
        }
    }

}
