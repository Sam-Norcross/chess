package client;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;

import static ui.EscapeSequences.*;
import static ui.PrintUtils.displayBoard;

public class Client {

    private final ServerFacade serverFacade;
    private String username;
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
        username = null;
        authToken = null;
        gameIDs = null;
        currentGame = null;
        playerColor = null;

        this.notificationHandler = notificationHandler;

        //Can use serverFacade.clear(); here in a try/catch for testing purposes

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
                return displayBoard(currentGame, playerColor);
            } else if (command.equals("leave") && authToken != null && currentGame != null) {
                return leaveGame();
            } else if (command.equals("move") && authToken != null && currentGame != null) {
                return makeMove(tokens);
            } else if (command.equals("resign") && authToken != null && currentGame != null) {
                return resign();
            } else if (command.equals("show") && authToken != null && currentGame != null) {
                return show();
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
            username = tokens[1];
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
            int gameID = serverFacade.createGame(new CreateRequest(authToken, tokens[1])).gameID();
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

            ws.connect(username, authToken, gameID, color);

            GameData gameData = serverFacade.joinGame(new JoinRequest(authToken, gameID, color));
            currentGame = gameData;
            playerColor = ChessGame.TeamColor.WHITE;

            return "";//displayBoard(gameData, color) + "\nJoined game " + listedID + " as " + color;
            // TODO--^^^ replaced in WebSocketFacade
        }  catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Error: both a game number and player color must be provided");
        } catch (Exception e) {
            throw new Exception("Error: invalid request");
        }
    }

    public String observeGame(String[] tokens) throws Exception{
        int gameID = Integer.parseInt(tokens[1]);
        int listedID = gameIDs.get(gameID);

        //REMOVE IN PHASE 6
        GameData placeholder = new GameData(0, null, null,
                                    "PLACEHOLDER", new ChessGame());

        String boardString = "";//displayBoard(placeholder, ChessGame.TeamColor.WHITE);

        return "Observing game " + listedID + "\n" + boardString;
    }

    private String leaveGame() {
        return null;
    }

    private String makeMove(String[] tokens) throws Exception {
        ChessPosition start = stringToPosition(tokens[1]);
        ChessPosition end = stringToPosition(tokens[2]);

        //TODO--add pawn promotion functionality--use tokens[3]

        ChessMove move = new ChessMove(start, end);

        ws.makeMove(authToken, currentGame.gameID(), move);

        return "Move successful!";
    }

    private String resign() {
        return null;
    }

    private String show() {
        return null;
    }

    private ChessPosition stringToPosition(String location) {
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
                         move <START> <END> --move a piece
                         resign --forfeit the current game
                         show <PIECE> --shows possible moves""";
            }
        }
    }

//    private String displayBoard(GameData gameData, ChessGame.TeamColor color) {
//        ChessBoard board = gameData.game().getBoard();
//
//        String setLabelColors = SET_BG_COLOR_LIGHT_GREY +  SET_TEXT_COLOR_BLACK;
//        String resetColors = RESET_BG_COLOR + RESET_TEXT_COLOR;
//        String space = "  ";
//        String pad = " ";   //Half of a space
//
//        String rowLabel;
//        if (color == ChessGame.TeamColor.WHITE) {
//            rowLabel = setLabelColors + space + space +
//                    "A" + space + "B" + space + "C" + space + "D" + space +
//                    "E" + space + "F" + space + "G" + space + "H" + space +
//                    space + resetColors + "\n";
//        } else {
//            rowLabel = setLabelColors + space + space +
//                    "H" + space + "G" + space + "F" + space + "E" + space +
//                    "D" + space + "C" + space + "B" + space + "A" + space +
//                    space + resetColors + "\n";
//
//        }
//
//        String boardString = "";
//        String currentColor = SET_BG_COLOR_BLACK;
//        for (int r = 8; r >= 1; r--) {
//            int currentRow = r;
//            if (color == ChessGame.TeamColor.BLACK) {
//                currentRow = 9 - r;
//            }
//
//            boardString += setLabelColors + pad + currentRow + pad;
//            for (int c = 1; c <= 8; c++) {
//                int currentColumn = c;
//                if (color == ChessGame.TeamColor.BLACK) {
//                    currentColumn = 9 - c;
//                }
//
//                currentColor = updateSquareColor(currentColor);
//                boardString += currentColor + SET_TEXT_COLOR_RED + getSymbol(board.getPiece(new ChessPosition(currentRow, currentColumn)));
//            }
//            boardString += setLabelColors + pad + currentRow + pad + resetColors + "\n";
//            currentColor = updateSquareColor(currentColor);
//        }
//
//        return "\n" + rowLabel + boardString + rowLabel + resetColors;
//    }

//    private String updateSquareColor(String color) {
//        if (color.equals(SET_BG_COLOR_WHITE)) {
//            return SET_BG_COLOR_BLACK;
//        } else {
//            return SET_BG_COLOR_WHITE;
//        }
//    }
//
//    private String getSymbol(ChessPiece piece) {
//        if (piece == null) {
//            return EMPTY;
//        }
//
//        String symbol = EMPTY;
//        ChessPiece.PieceType type = piece.getPieceType();
//        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            if (type == ChessPiece.PieceType.PAWN) {
//                symbol = WHITE_PAWN;
//            } else if (type == ChessPiece.PieceType.ROOK) {
//                symbol = WHITE_ROOK;
//            } else if (type == ChessPiece.PieceType.KNIGHT) {
//                symbol = WHITE_KNIGHT;
//            } else if (type == ChessPiece.PieceType.BISHOP) {
//                symbol = WHITE_BISHOP;
//            } else if (type == ChessPiece.PieceType.KING) {
//                symbol = WHITE_KING;
//            } else if (type == ChessPiece.PieceType.QUEEN) {
//                symbol = WHITE_QUEEN;
//            }
//        } else {
//            if (type == ChessPiece.PieceType.PAWN) {
//                symbol = BLACK_PAWN;
//            } else if (type == ChessPiece.PieceType.ROOK) {
//                symbol = BLACK_ROOK;
//            } else if (type == ChessPiece.PieceType.KNIGHT) {
//                symbol = BLACK_KNIGHT;
//            } else if (type == ChessPiece.PieceType.BISHOP) {
//                symbol = BLACK_BISHOP;
//            } else if (type == ChessPiece.PieceType.KING) {
//                symbol = BLACK_KING;
//            } else if (type == ChessPiece.PieceType.QUEEN) {
//                symbol = BLACK_QUEEN;
//            }
//        }
//
//        return symbol;
//    }

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

}
