package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        userDAO.clearUsers();
        userDAO.clearAuth();
        gameDAO.clearGames();
    }

}
