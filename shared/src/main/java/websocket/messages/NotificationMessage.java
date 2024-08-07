package websocket.messages;



public class NotificationMessage extends ServerMessage {
    public enum NotificationType {
        PLAYER_CONNECT,
        OBSERVER_CONNECT,
        MADE_MOVE,
        LEFT_GAME,
        RESIGNED_GAME,
        CHECK,
        CHECKMATE,
        STALEMATE
    }

    NotificationType type;
    String message;

    public NotificationMessage(NotificationType type, String message) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.type = type;
        this.message = message;
    }

    public NotificationType getNotificationType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
