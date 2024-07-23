package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public interface GameDAO {

    GameData createGame(String gameName);

    void clearGames();

    GameData getGame(int gameID);

    void updateGame(int gameID, GameData gameData);

    ArrayList<GameData> getGames();

}
