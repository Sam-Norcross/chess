package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    HashMap<String, UserData> users;
    HashMap<String, AuthData> authData;

    public MemoryUserDAO() {
        users = new HashMap<>();
        authData = new HashMap<>();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        authData.put(auth.username(), auth);
    }

}
