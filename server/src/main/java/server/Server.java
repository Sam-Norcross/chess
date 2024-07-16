package server;

import model.UserData;
import service.UserService;
import spark.*;
import com.google.gson.Gson;


public class Server {

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

    private String register(Request req, Response res) throws Exception {
        UserData data = new Gson().fromJson(req.body(), UserData.class);
        UserService.register(data);

        return "RETURNED"; //TODO--JSON string here
    }

}

