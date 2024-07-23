package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    private UserDAO userDAO;

    private UserData userBob = new UserData("Bob", "12345", "bob@gmail.com");

    @BeforeEach
    public void init() throws Exception {
        userDAO = new SQLUserDAO();

        userDAO.clearUsers();
        userDAO.clearAuth();
    }

    @Test
    public void createNewUser() throws DataAccessException {
        assertDoesNotThrow(() -> userDAO.createUser(userBob));
    }

    @Test
    public void createNullUser() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> userDAO.createUser(null));
    }

    @Test
    public void getUser() throws DataAccessException {
        userDAO.createUser(userBob);
        assertDoesNotThrow(() -> userDAO.getUser(userBob.username()));
        assertEquals(userBob.username(), userDAO.getUser(userBob.username()).username());
        assertEquals(userBob.email(), userDAO.getUser(userBob.username()).email());
        assertTrue(BCrypt.checkpw(userBob.password(), userDAO.getUser(userBob.username()).password()));
    }

    @Test
    public void getNonExistingUser() throws DataAccessException {
        userDAO.createUser(userBob);
        assertThrows(DataAccessException.class, () -> userDAO.getUser("Fred"));
    }

    @Test
    public void createAuth() throws DataAccessException {
        userDAO.createUser(userBob);
        AuthData auth = new AuthData(userBob.username(), UUID.randomUUID().toString());
        assertDoesNotThrow(() -> userDAO.createAuth(auth));
    }

    @Test
    public void createNullAuth() throws DataAccessException {
        userDAO.createUser(userBob);
        assertThrows(DataAccessException.class, () -> userDAO.createAuth(null));
    }

    @Test
    public void getAuth() throws DataAccessException {
        userDAO.createUser(userBob);
        String authToken = UUID.randomUUID().toString();
        userDAO.createAuth(new AuthData(userBob.username(), authToken));
        assertDoesNotThrow(() -> userDAO.getAuth(authToken));
        assertEquals(userBob.username(), userDAO.getAuth(authToken).username());
    }

    @Test
    public void getNonExistingAuth() throws DataAccessException {
        userDAO.createUser(userBob);
        String authToken = UUID.randomUUID().toString();
        userDAO.createAuth(new AuthData(userBob.username(), authToken));
        assertThrows(DataAccessException.class, () -> userDAO.getAuth(UUID.randomUUID().toString()));
    }

    @Test
    public void removeAuth() throws DataAccessException {
        userDAO.createUser(userBob);
        String authToken = UUID.randomUUID().toString();
        userDAO.createAuth(new AuthData(userBob.username(), authToken));
        assertDoesNotThrow(() -> userDAO.removeAuth(authToken));
        assertThrows(DataAccessException.class, () -> userDAO.getAuth(authToken));
    }

    @Test
    public void removeNonExistingAuth() throws DataAccessException {
        userDAO.createUser(userBob);
        String authToken = UUID.randomUUID().toString();
        userDAO.createAuth(new AuthData(userBob.username(), authToken));
        assertDoesNotThrow(() -> userDAO.removeAuth(UUID.randomUUID().toString()));
    }

}