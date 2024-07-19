package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private HashMap<String, UserData> users;
    private HashMap<String, AuthData> authData;

    public MemoryUserDAO() {
        users = new HashMap<>();
        authData = new HashMap<>();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData auth) {
        authData.put(auth.username(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData auth : authData.values()) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public String getUsernameFromAuth(String authToken) {
        for (String username : authData.keySet()) {
            if (authData.get(username).authToken().equals(authToken)) {
                return username;
            }
        }
        return null;
    }

    @Override
    public void removeAuth(String authToken) {
        for (AuthData auth : authData.values()) {
            if (auth.authToken().equals(authToken)) {
                authData.remove(auth.username());
            }
        }
    }


    @Override
    public void clearUsers() {
        users = new HashMap<>();
    }

    @Override
    public void clearAuth() {
        authData = new HashMap<>();
    }

}
