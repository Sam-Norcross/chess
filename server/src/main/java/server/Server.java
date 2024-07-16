package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.*;
import com.google.gson.Gson;


public class Server {

    private final UserService userService;

    public Server() {
        this.userService = new UserService(new MemoryUserDAO());
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);    //Body: { "username":"", "password":"", "email":"" }

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

    private String register(Request req, Response res) throws Exception {
        String authJson;
        Gson serializer = new Gson();
        try {
            UserData data = serializer.fromJson(req.body(), UserData.class);
            AuthData auth = userService.register(data);
            authJson = serializer.toJson(auth);
            System.out.println(authJson);
        } catch (DataAccessException ex) {
            res.status(403);
            authJson = serializer.toJson(ex); //"{ \"message\": \"Error: already taken\" }";
            System.out.println("CCC");
            System.out.println(authJson);
        }



        return authJson;
    }

}

