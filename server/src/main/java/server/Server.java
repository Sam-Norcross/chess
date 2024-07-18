package server;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import com.google.gson.Gson;


public class Server {

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        this.userService = new UserService(userDAO);
        this.gameService = new GameService(gameDAO);
        this.clearService = new ClearService(userDAO, gameDAO);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);    //Body: { "username":"", "password":"", "email":"" }
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);

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
        String authJson;
        Gson serializer = new Gson();


        try {
            UserData data = serializer.fromJson(req.body(), UserData.class);
            AuthData auth = userService.register(data);
            authJson = serializer.toJson(auth);
        } catch (DataAccessException ex) {
            res.status(403);
            authJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
        } catch (NullPointerException ex) {
            res.status(400);
            authJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
        }


        return authJson;
    }

    private String login(Request req, Response res) throws Exception {
        String authJson;
        Gson serializer = new Gson();

        try {
            UserData data = serializer.fromJson(req.body(), UserData.class);
            AuthData auth = userService.login(data);
            authJson = serializer.toJson(auth);
        } catch (DataAccessException ex) {
            authJson = "{ \"message\": \"" + ex.getMessage() + "\" }";
            res.status(401); //TODO--does this need to have more error codes? there is also a 500 in the specs
        }

        return authJson;
    }

    private String logout(Request req, Response res) throws Exception {
        String authJson;
        Gson serializer = new Gson();

        try {

            authJson = "{}";
        } catch (Exception ex) {
            authJson = "TEST (ERROR)";
        }

        return authJson;
    }




    private String clear(Request req, Response res) throws Exception {
        String authJson = "{}";
        Gson serializer = new Gson();
        clearService.clear();
        return authJson;
    }

}

