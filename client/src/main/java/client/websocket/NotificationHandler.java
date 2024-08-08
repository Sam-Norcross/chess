package client.websocket;


import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notification);

    void handleError(ErrorMessage errorMessage);

}
