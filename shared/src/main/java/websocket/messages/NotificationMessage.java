package websocket.messages;

public record NotificationMessage(NotificationType type, String message) {
    public enum NotificationType {
        PLAYER_CONNECT,
        OBSERVER_CONNECT,
        MADE_MOVE,
        LEFT_GAME,
        RESIGNED_GAME,
        CHECK,
        CHECKMATE
    }
}
