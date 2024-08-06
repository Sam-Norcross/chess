package websocket.commands;

import chess.ChessGame;
import chess.ChessPiece;

public class ConnectCommand extends UserGameCommand {

    String username;
    ChessGame.TeamColor color;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, String username) {
        super(commandType, authToken, gameID);
        this.username = username;
        color = null;
    }

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID,
                            String username, ChessGame.TeamColor color) {
        super(commandType, authToken, gameID);
        this.username = username;
        this.color = color;
    }

    public String getUsername() {
        return username;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

}

