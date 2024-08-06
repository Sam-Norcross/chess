package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;


public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Set<Session> sessions) {
        connections.put(gameID, sessions);
    }

    public void add(int gameID, Session session) {
        Set<Session> sessions = connections.get(gameID);
        if (sessions == null) {
            sessions =  new HashSet<Session>();
            connections.put(gameID, sessions);
        }
        sessions.add(session);
    }

    public void remove(int gameID) {
        connections.remove(gameID);
    }

    public void broadcast(int gameID, NotificationMessage notification, Session exclude) throws IOException {
        for (var session : connections.get(gameID)) {
            if (session.isOpen()) {
                if (!session.equals(exclude)) {
                    session.getRemote().sendString(new Gson().toJson(notification));
                }
            }
        }
    }

}
