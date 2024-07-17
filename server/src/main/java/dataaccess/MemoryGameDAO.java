package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private HashMap<Integer, GameData> games; //GameID, GameData?

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    public GameData createGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
        return gameData;
    }

    public void clearGames() {
        games = new HashMap<>();
    }

}
