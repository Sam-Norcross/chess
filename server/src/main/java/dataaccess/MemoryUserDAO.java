package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private HashMap<String, UserData> users;
    private HashMap<String, AuthData> authData; //authToken, authData

    public MemoryUserDAO() {
        users = new HashMap<>();
        authData = new HashMap<>();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public boolean checkPassword(String username, String password) {
        return password.equals(users.get(username).password());
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData auth) {
        authData.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authData.get(authToken);
    }

    @Override
    public void removeAuth(String authToken) {
        authData.remove(authToken);
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
