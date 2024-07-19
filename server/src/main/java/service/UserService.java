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

    public AuthData register(UserData user) throws DataAccessException, NullPointerException {
        AuthData auth;
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new NullPointerException("Error: invalid request");
        }
        if (userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("Error: already taken");
        } else {
            userDAO.createUser(user);
            String authToken = UUID.randomUUID().toString();
            auth = new AuthData(user.username(), authToken);
            userDAO.createAuth(auth);
        }

        return auth;
    }

    public AuthData login(UserData user) throws DataAccessException {
        String username = user.username();
        AuthData auth;
        UserData userData = userDAO.getUser(username);

        if (userData == null) {
            throw new DataAccessException("Error: invalid request");
        } else {
            if (!username.equals(userData.username()) || !user.password().equals(userData.password())) {
                throw new DataAccessException("Error: MESSAGE HERE (incorrect login info)");
            } else {

                String authToken = UUID.randomUUID().toString();
                auth = new AuthData(user.username(), authToken);
                userDAO.createAuth(auth);
            }
        }
        return auth;
    }

    public void logout(String authToken) throws DataAccessException, NullPointerException {
        AuthData authFromData = userDAO.getAuth(authToken);
        if (authFromData == null) {
            throw new DataAccessException("Error: unauthorized");
        } else {
            userDAO.removeAuth(authToken);
        }
    }

}
