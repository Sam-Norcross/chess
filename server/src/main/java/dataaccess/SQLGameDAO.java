package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    //games table: int gameID, whiteUsername, blackUsername, gameName, ChessGame game (<int, gameData>)
    private String gamesInit = """
                            CREATE TABLE IF NOT EXISTS games (
                            gameID int,
                            whiteUsername varchar(255),
                            blackUsername varchar(255),
                            gameName varchar(255),
                            game LONGTEXT
                            );
                        """;

    private int nextGameID;


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

        nextGameID = 1;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        String statementString = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game)" +
                                    "VALUES (?, ?, ?, ?, ?)";

        GameData gameData = new GameData(nextGameID++, null, null, gameName, new ChessGame());

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                Gson serializer = new Gson();

                statement.setInt(1, gameData.gameID());
                statement.setString(2, gameData.whiteUsername());
                statement.setString(3, gameData.blackUsername());
                statement.setString(4, gameData.gameName());
                statement.setString(5, serializer.toJson(gameData.game()));
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameData;
    }

    @Override
    public void clearGames() throws DataAccessException {
        String statementString = "DELETE FROM games";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        nextGameID = 1;
    }

    @Override
    public GameData getGame(int gameID) {
        GameData game;
        String queryString = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement query = connection.prepareStatement(queryString)) {
                query.setInt(1, gameID);
                ResultSet result = query.executeQuery();

                Gson serializer = new Gson();
                result.next();
                gameID = result.getInt(1);
                String whiteUsername = result.getString(2);
                String blackUsername = result.getString(3);
                String gameName = result.getString(4);
                ChessGame chessGame = serializer.fromJson(result.getString(5), ChessGame.class);
                game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                result.close();
            }
            connection.close();
        } catch (Exception e) {
            return null;
        }

        return game;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {

    }

    @Override
    public ArrayList<GameData> getGames() {
        return null;
    }

}
