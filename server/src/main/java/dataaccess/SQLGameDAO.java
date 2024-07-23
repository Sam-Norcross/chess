package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    //games table: int gameID, whiteUsername, blackUsername, gameName, ChessGame game (<int, gameData>)
    String gamesInit = """
                            CREATE TABLE IF NOT EXISTS games (
                            gameID int,
                            whiteUsername varchar(255),
                            blackUsername varchar(255),
                            gameName varchar(255),
                            game varchar(255)
                            );
                        """;

    public SQLGameDAO() { //throws DataAccessException {

        try {

            DatabaseManager.createDatabase();
            try {
                Connection connection = DatabaseManager.getConnection();
                try (PreparedStatement preparedStatement = connection.prepareStatement(gamesInit)) {
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }

        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public GameData createGame(String gameName) {
        return null;
    }

    @Override
    public void clearGames() {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {

    }

    @Override
    public ArrayList<GameData> getGames() {
        return null;
    }

}
