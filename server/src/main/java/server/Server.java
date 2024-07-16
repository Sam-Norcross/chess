package server;

import model.AuthData;
import model.UserData;
import service.UserService;
import spark.*;
import com.google.gson.Gson;


public class Server {

    private final UserService userService;

    public Server(UserService userService) {
        this.userService = userService;
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
        UserData data = new Gson().fromJson(req.body(), UserData.class);
        AuthData auth = userService.register(data);

        String authJson = new Gson().toJson(auth);
        System.out.println(authJson);

        return authJson; //TODO--JSON string here
    }

}

