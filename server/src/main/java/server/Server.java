package server;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import service.*;
import spark.*;
import com.google.gson.Gson;
import websocket.WebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Server {

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    private final WebSocketHandler webSocketHandler;

    public Server() {
        UserDAO userDAO = new SQLUserDAO(); //MemoryUserDAO();
        GameDAO gameDAO = new SQLGameDAO(); //MemoryGameDAO();
        this.userService = new UserService(userDAO);
        this.gameService = new GameService(userDAO, gameDAO);
        this.clearService = new ClearService(userDAO, gameDAO);

        webSocketHandler = new WebSocketHandler(this.userService, this.gameService, this.clearService);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }




    //Handler methods

    private String register(Request req, Response res) {
        String resultJson;
        Gson serializer = new Gson();

        try {
            UserData data = serializer.fromJson(req.body(), UserData.class);
            AuthData auth = userService.register(data);
            resultJson = serializer.toJson(auth);
        } catch (DataAccessException ex) {
            res.status(403);
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
        } catch (NullPointerException ex) {
            res.status(400);
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
        }

        return resultJson;
    }

    private String login(Request req, Response res) {
        String resultJson;
        Gson serializer = new Gson();

        try {
            UserData data = serializer.fromJson(req.body(), UserData.class);
            AuthData auth = userService.login(data);
            resultJson = serializer.toJson(auth);
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401);
        }

        return resultJson;
    }

    private String logout(Request req, Response res) {
        String resultJson;
        String authToken = req.headers("Authorization");

        try {
            userService.logout(authToken);
            resultJson = "{}";
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401);
        }
        return resultJson;
    }

    private String listGames(Request req, Response res) {
        String resultJson;
        Gson serializer = new Gson();
        String authToken = req.headers("Authorization");
        try {
            ArrayList<GameData> games = gameService.listGames(authToken);
            resultJson = "{ \"games\": " + serializer.toJson(games) + " }";
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401);
        }
        return resultJson;
    }

    private String createGame(Request req, Response res) {
        String resultJson;
        Gson serializer = new Gson();

        String authToken = req.headers("Authorization");
        String gameName = serializer.fromJson(req.body(), CreateRequest.class).gameName();

        try {
            GameData gameData = gameService.createGame(new CreateRequest(authToken, gameName));
            resultJson = serializer.toJson(gameData);
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401);
        }

        return resultJson;
    }

    private String joinGame(Request req, Response res) {
        Gson serializer = new Gson();
        String resultJson = "{}";
        String authToken = req.headers("Authorization");
        JoinRequest request = serializer.fromJson(req.body(), JoinRequest.class);
        request = new JoinRequest(authToken, request.gameID(), request.playerColor());

        try {

            //TODO--the tests don't pass with this, but it is needed for observers
//            if (request.playerColor() == null) {
//                GameData gameData = gameService.getGame(request.gameID());
//                return serializer.toJson(gameData);
//            }

            GameData gameData = gameService.joinGame(request);
            resultJson = serializer.toJson(gameData);

            String username;
            if (request.playerColor() == ChessGame.TeamColor.WHITE) {
                username = gameData.whiteUsername();
            } else {
                username = gameData.blackUsername();
            }

            webSocketHandler.loadGame(gameData, username, request.playerColor());



        } catch (NullPointerException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(400);
        }  catch (IllegalArgumentException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(403);
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401);
        } catch (IOException ex) {}

        return resultJson;
    }


    private String clear(Request req, Response res) throws Exception {
        String authJson = "{}";
        clearService.clear();
        return authJson;
    }

}

