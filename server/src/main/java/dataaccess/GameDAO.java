package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public interface GameDAO {

    GameData createGame(String gameName);

    int newGameID();

    void clearGames();

    GameData getGame(int gameID);

    void updateGame(int gameID, GameData gameData);

    HashMap<Integer, GameData> getGames();

}
