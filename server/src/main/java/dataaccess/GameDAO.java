package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public interface GameDAO {

    GameData createGame(String gameName);

    int newGameID();

    void clearGames();

    HashMap<Integer, GameData> getGames();

}
