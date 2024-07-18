package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private HashMap<Integer, GameData> games; //GameID, GameData?

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    public GameData createGame(String gameName) {
        GameData gameData = new GameData(newGameID(), null, null, gameName, new ChessGame());
        games.put(gameData.gameID(), gameData);
        return gameData;
    }

    public int newGameID() {
        return games.size() + 1;
    }

    public void clearGames() {
        games = new HashMap<>();
    }

    public HashMap<Integer, GameData> getGames() {
        return games;
    }

}
