import chess.ChessGame;
import model.*;
import server.ServerFacade;

import java.util.ArrayList;

public class Client {

    private final ServerFacade serverFacade;
    private String authToken;


    public Client(String url) {
        serverFacade = new ServerFacade(url);
        authToken = null;




        try { //TODO--testing only
            serverFacade.clear();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }



    }

    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String command = tokens[0];

            if (command.equals("help")) {
                return helpString();
            } else if (command.equals("register")) {
                return register(tokens);
            } else if (command.equals("login")) {
                return login(tokens);
            } else if (command.equals("logout")) {
                return logout();
            } else if (command.equals("list")) {
                return listGames();
            } else if (command.equals("create")) {
                return createGame(tokens);
            } else if (command.equals("join")) {
                return joinGame(tokens);

                //TODO--add observe
                //TODO--also make sure only pre-login commands can be accessed from pre-login,
                //      and that only post-login commands can be accessed from post-login

            } else if (command.equals("quit")) {
                return "quit";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return null;
    }

    private String helpString() {
        if (authToken == null) {
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

    public String register(String[] tokens) throws Exception{
        try {
            serverFacade.register(new UserData(tokens[1], tokens[2], tokens[3]));
            return "Successfully registered user " + tokens[1];
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String login(String[] tokens) throws Exception{
        try {
            authToken = serverFacade.login(new UserData(tokens[1], tokens[2], null)).authToken();
            return "Successfully logged in user " + tokens[1];
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String logout() throws Exception{
        try {
            serverFacade.logout(authToken);
            authToken = null;
            return "Successfully logged out!";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String listGames() throws Exception{
        try {
            ArrayList<GameData> games = serverFacade.listGames(authToken);

            if (games.size() == 0) {
                return "No games to list";
            }

            String gamesString = "";
            for (GameData gameData : games) {
                gamesString += gameData.toString() + "\n";
            }
            return gamesString;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String createGame(String[] tokens) throws Exception{
        try {
            int gameID = serverFacade.createGame(new CreateRequest(authToken, tokens[1])).gameID();
            return "Game successfully created with gameID " + gameID;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String joinGame(String[] tokens) throws Exception{
        try {
            int gameID = Integer.parseInt(tokens[1]);
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(tokens[2].toUpperCase());
            serverFacade.joinGame(new JoinRequest(authToken, gameID, color));
            return "Joined game " + gameID + " as " + color;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
