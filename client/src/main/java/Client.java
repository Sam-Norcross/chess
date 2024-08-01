import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class Client {

    private final ServerFacade serverFacade;
    private String authToken;
    private HashMap<Integer, Integer> gameIDs; //<list ID, gameID>


    public Client(String url) {
        serverFacade = new ServerFacade(url);
        authToken = null;
        gameIDs = null;




//        try { //TODO--testing only
//            serverFacade.clear();
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }



    }

    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String command = tokens[0];

            if (command.equals("register") && authToken == null) {
                return register(tokens);
            } else if (command.equals("login") && authToken == null) {
                String result = login(tokens);
                gameIDsInit();
                return result;
            } else if (command.equals("logout") && authToken != null) {
                return logout();
            } else if (command.equals("list") && authToken != null) {
                return listGames();
            } else if (command.equals("create") && authToken != null) {
                return createGame(tokens);
            } else if (command.equals("join") && authToken != null) {
                return joinGame(tokens);
            } else if (command.equals("observe") && authToken != null) {
                return observeGame(tokens);//observe(tokens);
            } else if (command.equals("quit")) {
                return "quit";
            } else {    //"help" and all unrecognized commands
                return helpString();
            }
        } catch (Exception ex) {
            return ex.getMessage();
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
            return """
                   Possible commands:
                    create <NAME> --create a new game
                    list --list all games
                    join <ID> [WHITE | BLACK] --join a game
                    observe <ID> --observe a game
                    logout --log the current user out
                    quit --exit the application
                    help --show possible commands""";
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
            throw new Exception(e.getMessage());
        }
    }

    public String createGame(String[] tokens) throws Exception{
        try {
            int gameID = serverFacade.createGame(new CreateRequest(authToken, tokens[1])).gameID();
            return "Game successfully created!";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String joinGame(String[] tokens) throws Exception{
        try {
            int gameID = Integer.parseInt(tokens[1]);
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(tokens[2].toUpperCase());

            int listedID = gameIDs.get(gameID);
            GameData gameData = serverFacade.joinGame(new JoinRequest(authToken, listedID, color));

            System.out.println(displayBoard(gameData, color));

            return "Joined game " + listedID + " as " + color;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String observeGame(String[] tokens) throws Exception{
        int gameID = Integer.parseInt(tokens[1]);
        int listedID = gameIDs.get(gameID);

        //TODO--remove in phase 6
        GameData PLACEHOLDER = new GameData(0, null, null,
                                    "PLACEHOLDER", new ChessGame());

        String boardString = displayBoard(PLACEHOLDER, ChessGame.TeamColor.WHITE);
        return "Observing game " + listedID + "\n" + boardString;
    }

    private String displayBoard(GameData gameData, ChessGame.TeamColor color) {
        ChessBoard board = gameData.game().getBoard();

        String setLabelColors = SET_BG_COLOR_LIGHT_GREY +  SET_TEXT_COLOR_BLACK;
        String resetColors = RESET_BG_COLOR + RESET_TEXT_COLOR;
        String SPACE = "  ";
        String PAD = " ";   //Half of a SPACE

        String rowLabel;
        if (color == ChessGame.TeamColor.WHITE) {
            rowLabel = setLabelColors + SPACE + SPACE +
                    "A" + SPACE + "B" + SPACE + "C" + SPACE + "D" + SPACE +
                    "E" + SPACE + "F" + SPACE + "G" + SPACE + "H" + SPACE +
                    SPACE + resetColors + "\n";
        } else {
            rowLabel = setLabelColors + SPACE + SPACE +
                    "H" + SPACE + "G" + SPACE + "F" + SPACE + "E" + SPACE +
                    "D" + SPACE + "C" + SPACE + "B" + SPACE + "A" + SPACE +
                    SPACE + resetColors + "\n";
        }

        String boardString = "";
        String currentColor = SET_BG_COLOR_BLACK;
        for (int r = 8; r >= 1; r--) {
            int currentRow = r;
            if (color == ChessGame.TeamColor.BLACK) {
                currentRow = 9 - r;
            }

            boardString += setLabelColors + PAD + currentRow + PAD;
            for (int c = 1; c <= 8; c++) {
                currentColor = updateSquareColor(currentColor);
                boardString += currentColor + SET_TEXT_COLOR_RED + getSymbol(board.getPiece(new ChessPosition(currentRow, c)));
            }
            boardString += setLabelColors + PAD + currentRow + PAD + resetColors + "\n";
            currentColor = updateSquareColor(currentColor);
        }

        return rowLabel + boardString + rowLabel + resetColors;
    }

    private String updateSquareColor(String color) {
        if (color.equals(SET_BG_COLOR_WHITE)) {
            return SET_BG_COLOR_BLACK;
        } else {
            return SET_BG_COLOR_WHITE;
        }
    }

    private String getSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        String symbol = EMPTY;
        ChessPiece.PieceType type = piece.getPieceType();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (type == ChessPiece.PieceType.PAWN) {
                symbol = WHITE_PAWN;
            } else if (type == ChessPiece.PieceType.ROOK) {
                symbol = WHITE_ROOK;
            } else if (type == ChessPiece.PieceType.KNIGHT) {
                symbol = WHITE_KNIGHT;
            } else if (type == ChessPiece.PieceType.BISHOP) {
                symbol = WHITE_BISHOP;
            } else if (type == ChessPiece.PieceType.KING) {
                symbol = WHITE_KING;
            } else if (type == ChessPiece.PieceType.QUEEN) {
                symbol = WHITE_QUEEN;
            }
        } else {
            if (type == ChessPiece.PieceType.PAWN) {
                symbol = BLACK_PAWN;
            } else if (type == ChessPiece.PieceType.ROOK) {
                symbol = BLACK_ROOK;
            } else if (type == ChessPiece.PieceType.KNIGHT) {
                symbol = BLACK_KNIGHT;
            } else if (type == ChessPiece.PieceType.BISHOP) {
                symbol = BLACK_BISHOP;
            } else if (type == ChessPiece.PieceType.KING) {
                symbol = BLACK_KING;
            } else if (type == ChessPiece.PieceType.QUEEN) {
                symbol = BLACK_QUEEN;
            }
        }

        return symbol;
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

}
