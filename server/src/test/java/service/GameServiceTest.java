package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private GameService gameService;
    private GameDAO gameDAO;

    @BeforeEach
    public void init() {
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);

        gameDAO = new MemoryGameDAO();
        gameService = new GameService(userDAO, gameDAO);
    }

    public String registerAndLogin() throws DataAccessException { //Helper function to simplify tests
        userService.register(new UserData("Bob", "12345", "bob@gmail.com"));
        AuthData auth = userService.login(new UserData("Bob", "12345", "bob@gmail.com"));
        return auth.authToken();
    }

    @Test
    public void createGame() throws DataAccessException {
        String authToken = registerAndLogin();
        assertDoesNotThrow(() -> gameService.createGame(authToken, "Bob's game"));
    }

    @Test
    public void createGameBadAuth() throws DataAccessException {
        String authToken = registerAndLogin();
        assertThrows(DataAccessException.class, () -> gameService.createGame(UUID.randomUUID().toString(), "Bob's game"));
    }

}
