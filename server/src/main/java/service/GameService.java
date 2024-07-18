package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public class GameService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        AuthData auth = userDAO.getAuth(authToken);
        GameData gameData;

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        else {
            gameData = gameDAO.createGame(gameName);
        }

        return gameData;
    }

//    public GameData joinGame() {
//
//    }
//
//    public Collection<GameData> listGames() {
//
//    }

}
