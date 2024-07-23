package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private GameService gameService;
    private GameDAO gameDAO;
    private ClearService clearService;

    @BeforeEach
    public void init() {
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);

        gameDAO = new MemoryGameDAO();
        gameService = new GameService(userDAO, gameDAO);

        clearService = new ClearService(userDAO, gameDAO);
    }

    public String registerAndLogin() throws DataAccessException { //Helper function to simplify tests
        userService.register(new UserData("Bob", "12345", "bob@gmail.com"));
        AuthData auth = userService.login(new UserData("Bob", "12345", "bob@gmail.com"));
        return auth.authToken();
    }

    public String registerAndLogin(UserData userData) throws DataAccessException { //Helper function to simplify tests
        userService.register(userData);
        AuthData auth = userService.login(new UserData("Bob", "12345", "bob@gmail.com"));
        return auth.authToken();
    }


    @Test
    public void singleClear() throws DataAccessException {
        UserData userData = new UserData("Bob", "12345", "bob@gmail.com");
        String authToken = registerAndLogin(userData);

        gameService.createGame(authToken, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> clearService.clear());
        assertEquals(gameDAO.getGames().size(), 0);
        assertEquals(userDAO.getUser(userData.username()), null);
    }

    @Test
    public void multipleClear() throws DataAccessException {
        UserData userData = new UserData("Bob", "12345", "bob@gmail.com");
        String authToken = registerAndLogin(userData);

        for (int i = 0; i < 100; i++) {
            gameService.createGame(authToken, UUID.randomUUID().toString());
        }

        assertDoesNotThrow(() -> clearService.clear());
        assertEquals(gameDAO.getGames().size(), 0);
        assertEquals(userDAO.getUser(userData.username()), null);
    }

}
