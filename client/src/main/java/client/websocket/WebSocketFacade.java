package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

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
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) { }

    public void connect(String username, String authToken, int gameID, ChessGame.TeamColor color) throws Exception {
        try {
            ConnectCommand command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, username, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

}
