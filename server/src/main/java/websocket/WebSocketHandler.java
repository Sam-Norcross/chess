package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;


import java.io.IOException;

import static websocket.commands.UserGameCommand.CommandType.*;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson serializer = new Gson();
        UserGameCommand command = serializer.fromJson(message, UserGameCommand.class);
        UserGameCommand.CommandType type = command.getCommandType();
        if (type == CONNECT) {
            ConnectCommand connectCommand = serializer.fromJson(message, ConnectCommand.class);
            connect(connectCommand.getGameID(), connectCommand.getUsername(), connectCommand.getColor(), session);
        } else if (type == MAKE_MOVE) {

        } else if (type == LEAVE) {

        } else if (type == RESIGN) {

        }
    }

    private void connect(int gameID, String username, ChessGame.TeamColor color, Session session) throws IOException {
        connections.add(gameID, session);
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

}
