package websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;
    public ChessGame.TeamColor color;

    public Connection(String authToken, Session session, ChessGame.TeamColor color) {
        this.authToken = authToken;
        this.session = session;
        this.color = color;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean verifyAuth(String authToken) {
        return this.authToken.equals(authToken);
    }

    public Session getSession() {
        return session;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public boolean isObserver() {
        return color == null;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "authToken='" + authToken + '\'' +
                ", color=" + color +
                '}';
    }
}
