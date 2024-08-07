package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

//    public void add(int gameID, Set<Connection> connectionSet) {
//        connections.put(gameID, connectionSet);
//    }

    public void add(int gameID, Connection connection) {
        Set<Connection> connectionSet = connections.get(gameID);
        if (connectionSet == null) {
            connectionSet =  new HashSet<Connection>();
            connections.put(gameID, connectionSet);
        }
        connectionSet.add(connection);
    }

    public void remove(int gameID) {
        connections.remove(gameID);
    }

    public void broadcast(int gameID, ServerMessage notification, Session exclude) throws IOException {
        for (var c : connections.get(gameID)) {
            if (c.getSession().isOpen()) {
                if (!c.getSession().equals(exclude)) {
                    c.send(new Gson().toJson(notification));
                }
            }
        }
    }

    public void broadcast(int gameID, ServerMessage notification, String excludeAuthToken) throws IOException {
        for (var c : connections.get(gameID)) {
            if (c.getSession().isOpen()) {
                if (!c.verifyAuth(excludeAuthToken)) {
                    c.send(new Gson().toJson(notification));
                }
            }
        }
    }

    //Send a ServerMessage to ONE client
    public void send(int gameID, String authToken, ServerMessage message) throws IOException {
        if (connections.containsKey(gameID)) {
            for (var c : connections.get(gameID)) {
                if (c.verifyAuth(authToken)) {
                    c.send(new Gson().toJson(message));
                }
            }
        }
    }

    public void sendToAll(int gameID, ServerMessage message) throws IOException {
        if (connections.containsKey(gameID)) {
            for (var c : connections.get(gameID)) {
                c.send(new Gson().toJson(message));
            }
        }
    }

    public void sendLoadGame(GameData gameData, String authToken) throws IOException {
        int gameID = gameData.gameID();
        if (connections.containsKey(gameID)) {
            for (var c : connections.get(gameID)) {
                if (c.verifyAuth(authToken)) {
                    c.send(new Gson().toJson(new LoadGameMessage(gameData, c.getColor())));
                }
            }
        }
    }

    public void sendLoadGameToAll(GameData gameData) throws IOException {
        int gameID = gameData.gameID();
        if (connections.containsKey(gameID)) {
            for (var c : connections.get(gameID)) {
                c.send(new Gson().toJson(new LoadGameMessage(gameData, c.getColor())));
            }
        }
    }

}
