package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import service.ClearService;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static websocket.commands.UserGameCommand.CommandType.*;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public WebSocketHandler (UserService userService, GameService gameService, ClearService clearService) {
        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson serializer = new Gson();
        UserGameCommand command = serializer.fromJson(message, UserGameCommand.class);
        UserGameCommand.CommandType type = command.getCommandType();

        if (type == CONNECT) {
            ConnectCommand connectCommand = serializer.fromJson(message, ConnectCommand.class);

            int gameID = connectCommand.getGameID();
            String username = connectCommand.getUsername();
            String authToken = connectCommand.getAuthToken();

            if (verifyAuthToken(authToken, session) && verifyGameID(gameID, session)) {
                connect(gameID, username, authToken, connectCommand.getColor(), session);
            }

        } else if (type == MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = serializer.fromJson(message, MakeMoveCommand.class);

            try {
                if (verifyAuthToken(makeMoveCommand.getAuthToken(), session)) {
                    makeMove(makeMoveCommand.getGameID(), makeMoveCommand.getMove(), makeMoveCommand.getAuthToken());
                }
            } catch (Exception e) {} //TODO--catch exception

        } else if (type == LEAVE) {

        } else if (type == RESIGN) {

        }

    }

    private void connect(int gameID, String username, String authToken, ChessGame.TeamColor color, Session session) throws IOException {
        connections.add(gameID, new Connection(authToken, session, color));

        loadGame(gameService.getGame(gameID), authToken, color);

        String message;
        if (color == ChessGame.TeamColor.WHITE) {
            message = username + " has joined the game as white.";
        } else if (color == ChessGame.TeamColor.BLACK) {
            message = username + " has joined the game as black.";
        } else {
            message = username + " has joined the game.";
        }
        NotificationMessage notification = new NotificationMessage(NotificationMessage.NotificationType.PLAYER_CONNECT, message);
        connections.broadcast(gameID, notification, session);
    }

    private void makeMove(int gameID, ChessMove move, String authToken) throws Exception {
        GameData gameData = gameService.getGame(gameID);
        ChessGame game = gameData.game();
        Collection<ChessMove> moves = game.validMoves(move.getStartPosition());
        boolean validMove = false;
        for (ChessMove chessMove : moves) {
            if (chessMove.equals(move)) {
                validMove = true;
            }
        }

        //Color of the piece being moved
        ChessGame.TeamColor pieceColor = game.getBoard().getPiece(move.getStartPosition()).getTeamColor();

        String username = userService.getAuthData(authToken).username();
        ChessGame.TeamColor userColor;
        if (gameData.whiteUsername().equals(username)) {
            userColor = ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername().equals(username)) {
            userColor = ChessGame.TeamColor.BLACK;
        } else {
            userColor = null;
        }

        if (userColor == null) {
            connections.send(gameID, authToken, new ErrorMessage("Error: cannot move as an observer"));
        } else if (game.isGameOver(pieceColor)) {
            connections.send(gameID, authToken, new ErrorMessage("Error: this game is already over"));
        } else if(game.getTeamTurn() != pieceColor || pieceColor != userColor) {
            connections.send(gameID, authToken, new ErrorMessage("Error: it is not your turn"));
        } else if (!validMove) {
            connections.send(gameID, authToken, new ErrorMessage("Error: invalid move"));
        } else {
            game.makeMove(move);
            GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), game);
            gameService.updateGame(gameID, updatedGameData);

            loadGame(updatedGameData, pieceColor);
            String message = username + " moved from " + move.getStartPosition().boardLocation() +
                    " to " + move.getEndPosition().boardLocation();

            connections.broadcast(gameID, new NotificationMessage(NotificationMessage.NotificationType.MADE_MOVE, message),
                    authToken);

            if (updatedGameData.game().isInCheck(pieceColor)) {
                connections.sendToAll(gameID, new NotificationMessage(NotificationMessage.NotificationType.CHECK,
                        username + " is in check"));
            } else if (updatedGameData.game().isInCheckmate(pieceColor)) {
                connections.sendToAll(gameID, new NotificationMessage(NotificationMessage.NotificationType.CHECKMATE,
                        username + " is in checkmate. " + username + " wins!"));
            } else if (updatedGameData.game().isInStalemate(pieceColor)) {
                connections.sendToAll(gameID, new NotificationMessage(NotificationMessage.NotificationType.CHECK,
                        username + " is in stalemate. " + updatedGameData.gameName() + "ends in a tie!"));
            }
        }

    }





    public void loadGame(GameData gameData, String authToken, ChessGame.TeamColor color) throws IOException {
//        connections.send(gameData.gameID(), username, new LoadGameMessage(gameData, color));

        connections.sendLoadGame(gameData, authToken);
    }

    public void loadGame(GameData gameData, ChessGame.TeamColor color) throws IOException {
//        connections.sendToAll(gameData.gameID(), new LoadGameMessage(gameData, color));

        connections.sendLoadGameToAll(gameData);
    }

    private String getUsername(GameData gameData, ChessGame.TeamColor color) {
        String username = gameData.whiteUsername();
        if (color == ChessGame.TeamColor.BLACK) {
            username = gameData.blackUsername();
        }
        return username;
    }

    private boolean verifyAuthToken(String authToken, Session session) throws IOException {
        if (!userService.authTokenExists(authToken)) {
            ErrorMessage errorMessage = new ErrorMessage("Error: invalid authToken");
            String message = new Gson().toJson(errorMessage);
            session.getRemote().sendString(message);
            return false;
        } else {
            return true;
        }
    }

    private boolean verifyGameID(int gameID, Session session) throws IOException {
        if (gameService.getGame(gameID) == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: invalid game ID");
            String message = new Gson().toJson(errorMessage);
            session.getRemote().sendString(message);
            return false;
        } else {
            return true;
        }
    }

//    private void sendError(int gameID, String authToken, String message) throws IOException {
//        connections.send(gameID, authToken, new ErrorMessage(message));
//    }

}
