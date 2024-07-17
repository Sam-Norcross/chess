package service;

import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

//    public Collection<GameData> listGames() {
//
//    }
//
//    public GameData createGame() {
//
//    }
//
//    public GameData joinGame() {
//
//    }
}
