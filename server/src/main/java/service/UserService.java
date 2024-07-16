package service;

import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    public AuthData register(UserData user) {
        String username = user.username();
        if (UserDAO.getUser(username) != null) {
            //Exception?
        } else {
            UserDAO.createUser(user);
            UserDAO.createAuth(/*AuthData*/);
        }
    }

    public AuthData login(UserData user) {

    }

    public void logout(UserData user) {

    }
}
