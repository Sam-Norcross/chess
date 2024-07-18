package server;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import service.*;
import spark.*;
import com.google.gson.Gson;

import java.util.HashMap;


public class Server {

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        this.userService = new UserService(userDAO);
        this.gameService = new GameService(userDAO, gameDAO);
        this.clearService = new ClearService(userDAO, gameDAO);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

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

    private String login(Request req, Response res) throws Exception {
        String resultJson;
        Gson serializer = new Gson();

        try {
            UserData data = serializer.fromJson(req.body(), UserData.class);
            AuthData auth = userService.login(data);
            resultJson = serializer.toJson(auth);
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401); //TODO--does this need to have more error codes? there is also a 500 in the specs
        }

        return resultJson;
    }

    private String logout(Request req, Response res) throws Exception {
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
            HashMap<Integer, GameData> games = gameService.listGames(authToken);
            resultJson = serializer.toJson(games);
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401);
        }

        return resultJson;
    }

    private String createGame(Request req, Response res) throws DataAccessException {
        String resultJson;
        String authToken = req.headers("Authorization");
        String gameName = req.body();

        try {
            GameData gameData = gameService.createGame(authToken, gameName);
            resultJson = "{ \"gameID\": " + gameData.gameID() + "}"; //new Gson().toJson(gameData.gameID());
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401);
        }


        return resultJson;
    }

    private String joinGame(Request req, Response res) throws DataAccessException {
        String resultJson = "TEST";
        String authToken = req.headers("Authorization");

        System.out.println(req.body()); //{"playerColor":"WHITE","gameID":1}
        //TODO--create a joinRequest record to serialize the JSON?

        try {
            gameService.joinGame(authToken, color, gameID);
        } catch (DataAccessException ex) {
            resultJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401);
        }

        return resultJson;
    }


    private String clear(Request req, Response res) throws Exception {
        String authJson = "{}";
        clearService.clear();
        return authJson;
    }

}

