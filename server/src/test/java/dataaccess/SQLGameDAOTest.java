package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    private String registerAndLogin() throws DataAccessException {
        userDAO.createUser(userBob);
        String authToken = UUID.randomUUID().toString();
        userDAO.createAuth(new AuthData(userBob.username(), authToken));
        return authToken;
    }

    @Test
    public void createGame() throws DataAccessException {
        registerAndLogin();
        assertDoesNotThrow(() -> gameDAO.createGame("Bob's game"));
    }

}
