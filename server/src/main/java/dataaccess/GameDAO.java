package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public interface GameDAO {

    GameData createGame(String gameName) throws DataAccessException;

    void clearGames() throws DataAccessException;

    GameData getGame(int gameID);

    void updateGame(int gameID, GameData gameData) throws DataAccessException;

    ArrayList<GameData> getGames();

}
