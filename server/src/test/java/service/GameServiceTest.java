package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import model.UserData;
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
    public void init() {
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);

        gameDAO = new MemoryGameDAO();
        gameService = new GameService(userDAO, gameDAO);
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
        assertDoesNotThrow(() -> gameService.createGame(authToken, "Bob's game"));
    }

    @Test
    public void createGameBadAuth() throws DataAccessException {
        registerAndLogin();
        assertThrows(DataAccessException.class, () -> gameService.createGame(UUID.randomUUID().toString(), "Bob's game"));
    }

    @Test
    public void listMultipleGames() throws DataAccessException {
        String authToken = registerAndLogin();
        for (int i = 0; i < 5; i++) {
            gameService.createGame(authToken, UUID.randomUUID().toString());
        }
        assertDoesNotThrow(() -> gameService.listGames(authToken));
        assertEquals(5, gameService.listGames(authToken).size());
    }

    @Test
    public void listMultipleGamesBadAuth() throws DataAccessException {
        String authToken = registerAndLogin();
        String badAuthToken = UUID.randomUUID().toString();
        for (int i = 0; i < 5; i++) {
            gameService.createGame(authToken, UUID.randomUUID().toString());
        }
        assertThrows(DataAccessException.class, () -> gameService.listGames(badAuthToken));
    }

    @Test
    public void joinGame() throws DataAccessException {
        String authToken = registerAndLogin();
        int gameID = gameService.createGame(authToken, "Bob's game").gameID();

        assertDoesNotThrow(() -> gameService.joinGame(authToken, new JoinRequest(gameID, ChessGame.TeamColor.WHITE)));
        GameData game = gameDAO.getGame(gameID);
        assertEquals(game.whiteUsername(), "Bob");

        assertEquals(1, gameDAO.getGames().size());
    }

    @Test
    public void joinGameColorTaken() throws DataAccessException {
        String authToken = registerAndLogin();
        String authToken2 = registerAndLogin(new UserData("Alice", "67890", "Alice@gmail.com"));
        int gameID = gameService.createGame(authToken2, "Alice's game").gameID();

        //Alice joins the game
        gameService.joinGame(authToken2, new JoinRequest(gameID, ChessGame.TeamColor.WHITE));

        //Bob tries to join the game with the same color
        assertThrows(IllegalArgumentException.class, () -> gameService.joinGame(authToken, new JoinRequest(gameID, ChessGame.TeamColor.WHITE)));
        GameData game = gameDAO.getGame(gameID);
        assertEquals(game.whiteUsername(), "Alice");
    }

    @Test
    public void joinGameBadID() throws DataAccessException {
        String authToken = registerAndLogin();
        int gameID = gameService.createGame(authToken, "Bob's game").gameID() + 5;

        assertThrows(NullPointerException.class, () -> gameService.joinGame(authToken, new JoinRequest(gameID, ChessGame.TeamColor.WHITE)));

    }
}
