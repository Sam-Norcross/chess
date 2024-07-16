package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        String username = user.username();
        AuthData auth = new AuthData(null, null);

        if (userDAO.getUser(username) != null) {
            //Exception?
        } else {
            userDAO.createUser(user);

            String authToken = UUID.randomUUID().toString();
            auth = new AuthData(user.username(), authToken);

            userDAO.createAuth(auth);
        }

        return auth;
    }

//    public AuthData login(UserData user) {
//
//    }
//
//    public void logout(UserData user) {
//
//    }
}
