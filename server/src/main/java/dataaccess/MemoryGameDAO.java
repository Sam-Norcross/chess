package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private HashMap<Integer, GameData> games; //GameID, GameData?

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    @Override
    public GameData createGame(String gameName) {
        GameData gameData = new GameData(newGameID(), null, null, gameName, new ChessGame());
        games.put(gameData.gameID(), gameData);
        return gameData;
    }

    @Override
    public int newGameID() {
        return games.size() + 1;
    }

    @Override
    public void clearGames() {
        games = new HashMap<>();
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {
        games.put(gameID, gameData);
    }

    @Override
    public ArrayList<GameData> getGames() {
        ArrayList<GameData> gamesList = new ArrayList<>();
        gamesList.addAll(games.values());
        return gamesList;
    }

}
