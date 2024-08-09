package client.websocket;

import chess.*;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

import static ui.PrintUtils.printBoard;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Gson serializer = new Gson();
                    ServerMessage serverMessage = serializer.fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();

                    if (messageType == ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadGameMessage loadGameMessage = serializer.fromJson(message, LoadGameMessage.class);
                        printBoard(loadGameMessage.getGame(), loadGameMessage.getColor());
                    } else if (messageType == ServerMessage.ServerMessageType.ERROR) {
                        ErrorMessage errorMessage = serializer.fromJson(message, ErrorMessage.class);
                        notificationHandler.handleError(errorMessage);
                    } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        NotificationMessage notification = serializer.fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notification);
                    }
                }
            });
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) { }

    public void connect(int gameID, String authToken) throws Exception {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        sendCommand(new MakeMoveCommand(authToken, gameID, move));
    }

    public void leave(int gameID, String authToken) throws Exception {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void resign(int gameID, String authToken) throws Exception {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }

    private void sendCommand(UserGameCommand command) throws Exception {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

}
