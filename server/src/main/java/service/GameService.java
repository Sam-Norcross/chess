package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GameService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        verifyAuth(authToken);
        return gameDAO.getGames();
    }

    public GameData createGame(CreateRequest createRequest) throws DataAccessException {
        verifyAuth(createRequest.authToken());
        return gameDAO.createGame(createRequest.gameName());
    }

    public GameData joinGame(JoinRequest request) throws DataAccessException, IllegalArgumentException, NullPointerException {
        verifyAuth(request.authToken());

        if (request == null || request.playerColor() == null) {
            throw new NullPointerException("Error: bad request");
        }

        GameData gameData = gameDAO.getGame(request.gameID());
        if (gameData == null) {
            throw new NullPointerException("Error: bad request");
        }

        ChessGame.TeamColor color = request.playerColor();
        String username = userDAO.getAuth(request.authToken()).username();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        if (color == ChessGame.TeamColor.WHITE) {
            if (gameData.whiteUsername() != null) {
                throw new IllegalArgumentException("Error: already taken");
            } else {
                whiteUsername = username;
            }
        } else {
            if (gameData.blackUsername() != null) {
                throw new IllegalArgumentException("Error: already taken");
            } else {
                blackUsername = username;
            }
        }

        GameData newGameData = new GameData(request.gameID(), whiteUsername, blackUsername, gameData.gameName(), gameData.game());
        gameDAO.updateGame(request.gameID(), newGameData);

        return newGameData;
    }

    private void verifyAuth(String authToken) throws DataAccessException {
        AuthData auth = userDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

}
