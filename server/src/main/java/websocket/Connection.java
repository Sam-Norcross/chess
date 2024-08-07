package websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String username;
    public Session session;
    public ChessGame.TeamColor color;

    public Connection(String username, Session session, ChessGame.TeamColor color) {
        this.username = username;
        this.session = session;
        if (color == null) {
            this.color = ChessGame.TeamColor.WHITE;
        } else {
            this.color = color;
        }
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public String getUsername() {
        return username;
    }

    public Session getSession() {
        return session;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
