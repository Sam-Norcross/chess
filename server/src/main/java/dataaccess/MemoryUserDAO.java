package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    HashMap<String, UserData> users;


    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public AuthData createAuth(AuthData auth) throws DataAccessException {

    }

}
