package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    public GameData createGame(String gameName);

    public int newGameID();

    public void clearGames();

}
