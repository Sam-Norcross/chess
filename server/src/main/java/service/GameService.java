package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Collection;
import java.util.HashMap;

public class GameService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public HashMap<Integer, GameData> listGames(String authToken) throws DataAccessException {
        verifyAuth(authToken);
        return gameDAO.getGames();
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        verifyAuth(authToken);
        return gameDAO.createGame(gameName);
    }

    public GameData joinGame(String authToken, ChessGame.TeamColor color, int gameID) throws DataAccessException {

    }

    private void verifyAuth(String authToken) throws DataAccessException {
        AuthData auth = userDAO.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

}
