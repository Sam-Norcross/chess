package server;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.google.gson.Gson;


public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public AuthData register(UserData userData) throws Exception {
        return makeRequest("POST", "/user", userData, AuthData.class);
    }

    public AuthData login(UserData userData) throws Exception {
        return makeRequest("POST", "/session", userData, AuthData.class);
    }

    public void logout(UserData userData) throws Exception {
        return makeRequest("DELETE", "/session", userData, null);
    }

    public ArrayList<GameData> listGames(UserData userData) throws Exception {
        return makeRequest("GET", "/game", userData, ArrayList.class);
    }

    public GameData createGame(UserData userData) throws Exception {
        return makeRequest("POST", "/game", userData, GameData.class);
    }

    public void joinGame(UserData userData) throws Exception {
        return makeRequest("PUT", "/game", userData, null);
    }

    public void clear(UserData userData) throws Exception {
        return makeRequest("DELETE", "/db", userData, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(responseClass, http);

        } catch (Exception ex) {
            throw new Exception("");
        }
    }

    private void writeBody(Object request, HttpURLConnection http) throws Exception {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private <T> T readBody(Class<T> responseClass, HttpURLConnection http) throws Exception {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

}