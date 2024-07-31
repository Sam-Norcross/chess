package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.CreateRequest;
import model.GameData;
import model.JoinRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private final UserData userBob = new UserData("Bob", "12345", "bob@email.com");
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        String url = "http://localhost:" + port;
        System.out.println(url);

        serverFacade = new ServerFacade(url);
    }

    @BeforeEach
    public void clear() throws Exception {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerNewUser() throws Exception {
        assertDoesNotThrow(() -> serverFacade.register(userBob));
    }

    @Test
    public void registerExistingUser() throws Exception {
        assertDoesNotThrow(() -> serverFacade.register(userBob));
        assertThrows(Exception.class, () -> serverFacade.register(userBob));
    }

    @Test
    public void loginExistingUser() throws Exception {
        serverFacade.register(userBob);
        assertDoesNotThrow(() -> serverFacade.login(userBob));
    }

    @Test
    public void loginNonExistingUser() throws Exception {
        assertThrows(Exception.class, () -> serverFacade.login(userBob));
    }

    @Test
    public void logoutExistingUser() throws Exception {
        serverFacade.register(userBob);
        String authToken = serverFacade.login(userBob).authToken();
        assertDoesNotThrow(() -> serverFacade.logout(authToken));
    }

    @Test
    public void logoutBadAuth() throws Exception {
        serverFacade.register(userBob);
        serverFacade.login(userBob);
        assertThrows(Exception.class, () -> serverFacade.logout(UUID.randomUUID().toString()));
    }

    @Test
    public void createGame() throws Exception {
        serverFacade.register(userBob);
        String authToken = serverFacade.login(userBob).authToken();
        assertDoesNotThrow(() -> serverFacade.createGame(new CreateRequest(authToken, "Bob's game")));
    }

    @Test
    public void createGameNullTest() throws Exception {
        serverFacade.register(userBob);
        String authToken = serverFacade.login(userBob).authToken();
        GameData gameData = serverFacade.createGame(new CreateRequest(authToken, "Bob's game"));
        assertNotNull(gameData);
        assertEquals(1, gameData.gameID());
        assertNull(gameData.whiteUsername());
        assertNull(gameData.blackUsername());
        assertEquals("Bob's game", gameData.gameName());
        assertNotNull(gameData.game());


    }

    @Test
    public void createGameBadAuth() throws Exception {
        serverFacade.register(userBob);
        serverFacade.login(userBob);
        assertThrows(Exception.class, () -> serverFacade.createGame(new CreateRequest(UUID.randomUUID().toString(), "Bob's game")));
    }

    @Test
    public void listMultiple() throws Exception {
        serverFacade.register(userBob);
        String authToken = serverFacade.login(userBob).authToken();
        for (int i = 0; i < 10; i++) {
            serverFacade.createGame(new CreateRequest(authToken, "Bob's game number " + i));
        }

        assertDoesNotThrow(() -> serverFacade.listGames(authToken));
        ArrayList<GameData> games = serverFacade.listGames(authToken);
        assertEquals(10, games.size());
    }

    @Test
    public void listBadAuth() throws Exception {
        serverFacade.register(userBob);
        String authToken = serverFacade.login(userBob).authToken();
        for (int i = 0; i < 10; i++) {
            serverFacade.createGame(new CreateRequest(authToken, "Bob's game number " + i));
        }

        assertThrows(Exception.class, () -> serverFacade.listGames(UUID.randomUUID().toString()));
    }

    @Test
    public void joinExistingGame() throws Exception {
        serverFacade.register(userBob);
        String authToken = serverFacade.login(userBob).authToken();
        serverFacade.createGame(new CreateRequest(authToken, "Bob's game"));
        assertDoesNotThrow(() -> serverFacade.joinGame(new JoinRequest(authToken, 1, ChessGame.TeamColor.WHITE)));
        assertNotNull(serverFacade.joinGame(new JoinRequest(authToken, 1, ChessGame.TeamColor.BLACK)).game());

    }

    @Test
    public void joinBadGameID() throws Exception {
        serverFacade.register(userBob);
        String authToken = serverFacade.login(userBob).authToken();
        serverFacade.createGame(new CreateRequest(authToken, "Bob's game"));
        assertThrows(Exception.class, () -> serverFacade.joinGame(new JoinRequest(authToken, 999, ChessGame.TeamColor.WHITE)));
    }

    @Test
    public void clearMultiple() throws Exception {
        UserData userAlice = new UserData("Alice", "67890", "alice@email.com");

        serverFacade.register(userBob);
        serverFacade.register(userAlice);
        String authTokenBob = serverFacade.login(userBob).authToken();
        String authTokenAlice = serverFacade.login(userAlice).authToken();

        for (int i = 0; i < 5; i++) {
            serverFacade.createGame(new CreateRequest(authTokenBob, "Bob's game number " + i));
        }
        for (int i = 0; i < 5; i++) {
            serverFacade.createGame(new CreateRequest(authTokenAlice, "Alice's game number " + i));
        }

        assertDoesNotThrow(() -> serverFacade.clear());
        assertThrows(Exception.class, () -> serverFacade.login(userBob));
        assertThrows(Exception.class, () -> serverFacade.login(userAlice));
    }

}
