package server;

import model.*;

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

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", authToken, null);
    }

    public ArrayList<GameData> listGames(String authToken) throws Exception {
        record ListGamesResponse(ArrayList<GameData> games) {}
        ListGamesResponse response = makeRequest("GET", "/game", authToken, ListGamesResponse.class);
        return response.games();
    }

    public GameData createGame(CreateRequest request) throws Exception {
        return makeRequest("POST", "/game", request, GameData.class);
    }

    public GameData joinGame(JoinRequest request) throws Exception {
        return makeRequest("PUT", "/game", request, GameData.class);
    }

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null);
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
            throw new Exception("Could not make request: " + ex.getMessage());
        }
    }

    private void writeBody(Object request, HttpURLConnection http) throws Exception {
        if (request instanceof String) {
            http.setRequestProperty("Authorization", (String) request);
        } else if (request != null) {

            if (request instanceof CreateRequest) {
                CreateRequest createRequest = (CreateRequest) request;
                http.setRequestProperty("Authorization", createRequest.authToken());
            } else if (request instanceof JoinRequest) {
                JoinRequest joinRequest = (JoinRequest) request;
                http.setRequestProperty("Authorization", joinRequest.authToken());
            }

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

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        int status = http.getResponseCode();
        if (status != 200) {
            throw new Exception("Response code was not 200");
        }
    }

}