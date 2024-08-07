package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
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
            connect(connectCommand.getGameID(), connectCommand.getUsername(), connectCommand.getColor(), session);
        } else if (type == MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = serializer.fromJson(message, MakeMoveCommand.class);

            try {
                makeMove(makeMoveCommand.getGameID(), makeMoveCommand.getMove());
            } catch (Exception e) {} //TODO--catch exception

        } else if (type == LEAVE) {

        } else if (type == RESIGN) {

        }

    }

    private void connect(int gameID, String username, ChessGame.TeamColor color, Session session) throws IOException {
        connections.add(gameID, new Connection(username, session, color));
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

    private void makeMove(int gameID, ChessMove move) throws Exception {
        GameData gameData = gameService.getGame(gameID);
        ChessGame game = gameData.game();
        Collection<ChessMove> moves = game.validMoves(move.getStartPosition());
        boolean validMove = false;
        for (ChessMove chessMove : moves) {
            if (chessMove.equals(move)) {
                validMove = true;
            }
        }

        ChessGame.TeamColor color = game.getBoard().getPiece(move.getStartPosition()).getTeamColor();
        String username = getUsername(gameData, color);

        if (!validMove) {
            connections.send(gameID, username, new ErrorMessage("Error: invalid move"));
        } else {
            game.makeMove(move);
            GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), game);
            gameService.updateGame(gameID, updatedGameData);

            loadGame(updatedGameData, color);
            String message = username + " moved from " + move.getStartPosition().boardLocation() +
                    " to " + move.getEndPosition().boardLocation();

            connections.broadcast(gameID, new NotificationMessage(NotificationMessage.NotificationType.MADE_MOVE, message),
                    username);

            if (updatedGameData.game().isInCheck(color)) {
                connections.sendToAll(gameID, new NotificationMessage(NotificationMessage.NotificationType.CHECK,
                        username + " is in check"));
            } else if (updatedGameData.game().isInCheckmate(color)) {
                connections.sendToAll(gameID, new NotificationMessage(NotificationMessage.NotificationType.CHECKMATE,
                        username + " is in checkmate. " + username + " wins!"));
            } else if (updatedGameData.game().isInStalemate(color)) {
                connections.sendToAll(gameID, new NotificationMessage(NotificationMessage.NotificationType.CHECK,
                        username + " is in stalemate. " + updatedGameData.gameName() + "ends in a tie!"));
            }
        }

    }





    public void loadGame(GameData gameData, String username, ChessGame.TeamColor color) throws IOException {
//        connections.send(gameData.gameID(), username, new LoadGameMessage(gameData, color));

        connections.sendLoadGame(gameData, username);
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

}
