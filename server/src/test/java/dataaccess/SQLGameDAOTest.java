package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {

    private UserDAO userDAO;
    private GameDAO gameDAO;

    private UserData userBob = new UserData("Bob", "12345", "bob@gmail.com");

    @BeforeEach
    public void init() throws Exception {
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();

        userDAO.clearUsers();
        userDAO.clearAuth();
        gameDAO.clearGames();
    }

    @Test
    public void createGame() throws DataAccessException {
        assertDoesNotThrow(() -> gameDAO.createGame("Bob's game"));
    }

    @Test public void createGameNameTooLong() throws DataAccessException {
        String name = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 15; i++) {
            name += name;
        }
        final String nameFinal = name;
        assertThrows(Exception.class, () -> gameDAO.createGame(nameFinal));
    }

    @Test
    public void clearGames() throws DataAccessException {
        gameDAO.createGame("Bob's game");
        gameDAO.createGame("Freds's game");
        gameDAO.createGame("Bob's 2nd game");
        gameDAO.createGame("John's game");
        assertDoesNotThrow(() -> gameDAO.clearGames());
    }

    @Test
    public void getGame() throws DataAccessException {
        gameDAO.createGame("Bob's game");
        assertDoesNotThrow(() -> gameDAO.getGame(1));
        assertEquals("Bob's game", gameDAO.getGame(1).gameName());
        assertNotNull(gameDAO.getGame(1).game());
    }

    @Test
    public void getNonExistentGame() throws DataAccessException {
        gameDAO.createGame("Bob's game");
        assertNull(gameDAO.getGame(2));
    }

    @Test
    public void updateGame() throws DataAccessException {
        gameDAO.createGame("Bob's game");
        GameData gameData = new GameData(1, "Bob", "Frank",
                                        "It's Frank's game now!!!", new ChessGame());
        assertDoesNotThrow(() -> gameDAO.updateGame(1, gameData));
        GameData newGame = gameDAO.getGame(1);
        assertEquals("Bob", newGame.whiteUsername());
        assertEquals("Frank", newGame.blackUsername());
        assertEquals("It's Frank's game now!!!", newGame.gameName());
    }

    @Test
    public void updateGameBadID() throws DataAccessException {
        gameDAO.createGame("Bob's game");
        GameData gameData = new GameData(5, "Bob", "Frank",
                "It's Frank's game now!!!", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(5, gameData));
    }

    @Test
    public void listMultipleGames() throws DataAccessException {
        gameDAO.createGame("Bob's game");
        gameDAO.createGame("Freds's game");
        gameDAO.createGame("Bob's 2nd game");
        gameDAO.createGame("John's game");
        assertDoesNotThrow(() -> gameDAO.getGames());
        assertEquals(4, gameDAO.getGames().size());
    }

    @Test
    public void listNoGames() throws DataAccessException {
        assertDoesNotThrow(() -> gameDAO.getGames());
        assertEquals(0, gameDAO.getGames().size());
    }

}
