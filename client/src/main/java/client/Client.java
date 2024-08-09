package client;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.*;
import websocket.messages.ErrorMessage;

import java.util.ArrayList;
import java.util.HashMap;

import static ui.EscapeSequences.*;
import static ui.PrintUtils.displayBoard;
import static ui.PrintUtils.showMoves;

public class Client {

    private final ServerFacade serverFacade;
    private String authToken;
    private HashMap<Integer, Integer> gameIDs; //<list ID, gameID>

    private GameData currentGame;
    private ChessGame.TeamColor playerColor;

    private String url;
    private WebSocketFacade ws;
    private NotificationHandler notificationHandler;


    public Client(String url, NotificationHandler notificationHandler) {
        this.url = url;
        serverFacade = new ServerFacade(url);
        authToken = null;
        gameIDs = null;
        currentGame = null;
        playerColor = null;

        this.notificationHandler = notificationHandler;

    }

    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String command = tokens[0];

            if (command.equals("register") && authToken == null) {
                register(tokens);
                return login(tokens);
            } else if (command.equals("login") && authToken == null) {
                String result = login(tokens);
                gameIDsInit();
                return result;
            } else if (command.equals("logout") && authToken != null && currentGame == null) {
                return logout();
            } else if (command.equals("list") && authToken != null && currentGame == null) {
                return listGames();
            } else if (command.equals("create") && authToken != null && currentGame == null) {
                return createGame(tokens);
            } else if (command.equals("join") && authToken != null && currentGame == null) {
                return joinGame(tokens);
            } else if (command.equals("observe") && authToken != null && currentGame == null) {
                return observeGame(tokens);
            } else if (command.equals("quit")) {
                return "quit";
            } else if (command.equals("redraw") && authToken != null && currentGame != null) {
                updateCurrentGame();
                return displayBoard(currentGame, playerColor);
            } else if (command.equals("leave") && authToken != null && currentGame != null) {
                return leaveGame();
            } else if (command.equals("move") && authToken != null && currentGame != null) {
                return makeMove(tokens);
            } else if (command.equals("resign") && authToken != null && currentGame != null) {
                return resign();
            } else if (command.equals("show") && authToken != null && currentGame != null) {
                return show(tokens);
            } else {    //"help" and all unrecognized commands
                return helpString();
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String[] tokens) throws Exception{
        try {
            serverFacade.register(new UserData(tokens[1], tokens[2], tokens[3]));
            return "Successfully registered user " + tokens[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Error: a username, password, and email must all be provided");
        } catch (Exception e) {
            throw new Exception("Error registering user " + tokens[1]);
        }
    }

    public String login(String[] tokens) throws Exception{
        try {
            authToken = serverFacade.login(new UserData(tokens[1], tokens[2], null)).authToken();
            ws = new WebSocketFacade(url, notificationHandler);
            return "Successfully logged in user " + tokens[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Error: both a username and password must be provided");
        } catch (Exception e) {
            throw new Exception("Unrecognized login info");
        }
    }

    public String logout() throws Exception{
        try {
            serverFacade.logout(authToken);
            authToken = null;
            return "Successfully logged out!";
        } catch (Exception e) {
            throw new Exception("Error: unable to log out");
        }
    }

    public String listGames() throws Exception{
        try {
            ArrayList<GameData> games = serverFacade.listGames(authToken);

            if (games.isEmpty()) {
                return "No games to list";
            }

            String gamesString = """
                                 Number\tGame Name\t\t\tWhite Username\t\tBlack Username
                                 ---------------------------------------------------------
                                 """;

            int i = 1;
            for (GameData gameData : games) {
                if (gameData != null) {
                    String whiteUsername = gameData.whiteUsername();
                    String blackUsername = gameData.blackUsername();
                    if (whiteUsername == null) {
                        whiteUsername = "None";
                    }
                    if (blackUsername == null) {
                        blackUsername = "None";
                    }

                    String nameSpace = " ";
                    for (int r = 0; r < 19 - gameData.gameName().length(); r++) {
                        nameSpace += " ";
                    }

                    String wUsernameSpace = " ";
                    for (int r = 0; r < 19 - whiteUsername.length(); r++) {
                        wUsernameSpace += " ";
                    }

                    gamesString += i++ + "\t\t" + gameData.gameName() + nameSpace + whiteUsername + wUsernameSpace
                            + blackUsername + "\n";
                }
            }

            gameIDsInit();

            return gamesString;
        } catch (Exception e) {
            throw new Exception("Error: unable to list games");
        }
    }

    public String createGame(String[] tokens) throws Exception{
        try {
            serverFacade.createGame(new CreateRequest(authToken, tokens[1]));
            return "Game successfully created!";
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Error: a game name must be specified");
        } catch (Exception e) {
            throw new Exception("Error: unable to create game");
        }
    }

    public String joinGame(String[] tokens) throws Exception{
        try {
            int listedID = Integer.parseInt(tokens[1]);
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(tokens[2].toUpperCase());

            int gameID = gameIDs.get(listedID);

            currentGame = serverFacade.joinGame(new JoinRequest(authToken, gameID, color));
            playerColor = color;

            ws.connect(gameID, authToken);


            return "";
        }  catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Error: both a game number and player color must be provided");
        } catch (Exception e) {
            throw new Exception("Error: invalid request");
        }
    }

    public String observeGame(String[] tokens) throws Exception{
        try {
            int listedID = Integer.parseInt(tokens[1]);
            int gameID = gameIDs.get(listedID);

            ws.connect(gameID, authToken);

            return "";
        }  catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Error: a game number must be specified");
        } catch (Exception e) {
            throw new Exception("Error: invalid request");
        }
    }

    private String leaveGame() throws Exception {
        try {
            ws.leave(currentGame.gameID(), authToken);
            removeGameData();
            return "You have successfully left the game!";
        } catch (Exception e) {
            throw new Exception("Error: invalid request");
        }
    }

    private String makeMove(String[] tokens) throws Exception {
        try {
            ChessPosition start = stringToPosition(tokens[1]);
            ChessPosition end = stringToPosition(tokens[2]);

            ChessMove move;
            if (tokens.length == 4) {
                move = new ChessMove(start, end, ChessPiece.PieceType.valueOf(tokens[3]));
            } else {
                move = new ChessMove(start, end);
            }

            //Update to most current game for later display purposes
            updateCurrentGame();

            ws.makeMove(authToken, currentGame.gameID(), move);

            currentGame.game().makeMove(move);

        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            notificationHandler.handleError(new ErrorMessage("Error: both chess positions must be valid"));
        }

        return "";

    }

    private String resign() throws Exception {
        try {
            ws.resign(currentGame.gameID(), authToken);
            removeGameData();
            return "";
        } catch (Exception e) {
            throw new Exception("Error: invalid request");
        }
    }

    private String show(String[] tokens) throws Exception {
        try {
            updateCurrentGame();
            ChessPosition position = stringToPosition(tokens[1]);
            return showMoves(currentGame, playerColor, currentGame.game().validMoves(position));
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            notificationHandler.handleError(new ErrorMessage("Error: must include a valid chess position"));
        }
        return null;
    }

    private void removeGameData() {
        currentGame = null;
        playerColor = null;
    }

    private ChessPosition stringToPosition(String location) throws Exception {
        try {
            String col = location.toLowerCase().substring(0, 1);
            int row = Integer.parseInt(location.substring(1, 2));
            int column = 0;
            if (col.equals("a")) {
                column = 1;
            } else if (col.equals("b")) {
                column = 2;
            } else if (col.equals("c")) {
                column = 3;
            } else if (col.equals("d")) {
                column = 4;
            } else if (col.equals("e")) {
                column = 5;
            } else if (col.equals("f")) {
                column = 6;
            } else if (col.equals("g")) {
                column = 7;
            } else if (col.equals("h")) {
                column = 8;
            }
            return new ChessPosition(row, column);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Error: invalid chess position");
        }
    }

    private String helpString() {
        if (authToken == null) {
            return """
                   Possible commands:
                    register <USERNAME> <PASSWORD> <EMAIL> --register a new user
                    login <USERNAME> <PASSWORD> --login an existing user
                    quit --exit the application
                    help --show possible commands""";
        } else {
            if (currentGame == null) {
                return """
                        Possible commands:
                         create <NAME> --create a new game
                         list --list all games
                         join <ID> [WHITE | BLACK] --join a game
                         observe <ID> --observe a game
                         logout --log the current user out
                         quit --exit the application
                         help --show possible commands""";
            } else {
                return """
                        Possible commands:
                         redraw --redraws the chess board
                         leave --leave the current game
                         move <START> <END> [PROMOTION TYPE] --move a piece
                         resign --forfeit the current game
                         show <PIECE> --shows possible moves""";
            }
        }
    }

    private void gameIDsInit() throws Exception {
        ArrayList<GameData> games = serverFacade.listGames(authToken);
        int i = 1;
        gameIDs = new HashMap<>();
        for (GameData gameData : games) {
            if (gameData != null) {
                gameIDs.put(i++, gameData.gameID());
            }
        }

    }

    private void updateCurrentGame() throws Exception {
        currentGame = serverFacade.joinGame(new JoinRequest(authToken, currentGame.gameID(), null));
    }

}
