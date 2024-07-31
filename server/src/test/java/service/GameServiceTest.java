package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private GameService gameService;
    private GameDAO gameDAO;

    @BeforeEach
    public void init() throws Exception {
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();

        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();

        userService = new UserService(userDAO);
        gameService = new GameService(userDAO, gameDAO);

        userDAO.clearUsers();
        userDAO.clearAuth();
        gameDAO.clearGames();
    }

    public String registerAndLogin(UserData user) throws DataAccessException { //Helper function to simplify tests
        userService.register(user);
        AuthData auth = userService.login(user);
        return auth.authToken();
    }

    public String registerAndLogin() throws DataAccessException { //Helper function to simplify tests
        return registerAndLogin(new UserData("Bob", "12345", "bob@gmail.com"));
    }

    @Test
    public void createGame() throws DataAccessException {
        String authToken = registerAndLogin();
        assertDoesNotThrow(() -> gameService.createGame(new CreateRequest(authToken, "Bob's game")));
    }

    @Test
    public void createGameBadAuth() throws DataAccessException {
        registerAndLogin();
        assertThrows(DataAccessException.class, () -> gameService.createGame(new CreateRequest(UUID.randomUUID().toString(), "Bob's game")));
    }

    @Test
    public void listMultipleGames() throws DataAccessException {
        String authToken = registerAndLogin();
        for (int i = 0; i < 5; i++) {
            gameService.createGame(new CreateRequest(authToken, UUID.randomUUID().toString()));
        }
        assertDoesNotThrow(() -> gameService.listGames(authToken));
        assertEquals(5, gameService.listGames(authToken).size());
    }

    @Test
    public void listMultipleGamesBadAuth() throws DataAccessException {
        String authToken = registerAndLogin();
        String badAuthToken = UUID.randomUUID().toString();
        for (int i = 0; i < 5; i++) {
            gameService.createGame(new CreateRequest(authToken, UUID.randomUUID().toString()));
        }
        assertThrows(DataAccessException.class, () -> gameService.listGames(badAuthToken));
    }

    @Test
    public void joinGame() throws DataAccessException {
        String authToken = registerAndLogin();
        int gameID = gameService.createGame(new CreateRequest(authToken, "Bob's game")).gameID();

        assertDoesNotThrow(() -> gameService.joinGame(new JoinRequest(authToken, gameID, ChessGame.TeamColor.WHITE)));
        GameData game = gameDAO.getGame(gameID);
        assertEquals(game.whiteUsername(), "Bob");
        assertNotNull(game.game());

        assertEquals(1, gameDAO.getGames().size());
    }

    @Test
    public void joinGameColorTaken() throws DataAccessException {
        String authToken = registerAndLogin();
        String authToken2 = registerAndLogin(new UserData("Alice", "67890", "Alice@gmail.com"));
        int gameID = gameService.createGame(new CreateRequest(authToken2, "Alice's game")).gameID();

        //Alice joins the game
        gameService.joinGame(new JoinRequest(authToken2, gameID, ChessGame.TeamColor.WHITE));

        //Bob tries to join the game with the same color
        assertThrows(IllegalArgumentException.class, () -> gameService.joinGame(new JoinRequest(authToken, gameID, ChessGame.TeamColor.WHITE)));
        GameData game = gameDAO.getGame(gameID);
        assertEquals(game.whiteUsername(), "Alice");
    }

    @Test
    public void joinGameBadID() throws DataAccessException {
        String authToken = registerAndLogin();
        int gameID = gameService.createGame(new CreateRequest(authToken, "Bob's game")).gameID() + 5;

        assertThrows(NullPointerException.class, () -> gameService.joinGame(new JoinRequest(authToken, gameID, ChessGame.TeamColor.WHITE)));

    }
}
