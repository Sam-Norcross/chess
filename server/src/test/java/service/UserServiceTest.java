package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;


    @BeforeEach
    public void init() {//} throws Exception {
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);


//        userDAO = new SQLUserDAO();
//        userService = new UserService(userDAO);
//        userDAO.clearUsers();
//        userDAO.clearAuth();
    }

    @Test
    public void registerNewUser() {
        assertDoesNotThrow(() -> userService.register(new UserData("Bob", "12345", "bob@gmail.com")));
    }

    @Test
    public void registerExistingUser() throws DataAccessException {
        AuthData auth1 = userService.register(new UserData("Bob", "12345", "bob@gmail.com"));
        assertThrows(DataAccessException.class, () -> userService.register(new UserData("Bob", "12345", "bob@gmail.com")));
    }

    @Test
    public void registerInvalidUser() {
        assertThrows(NullPointerException.class, () -> userService.register(new UserData("Bob", null, "bob@gmail.com")));
    }

    @Test
    public void loginValidUser() throws Exception {
        userService.register(new UserData("Bob", "12345", "bob@gmail.com"));
        assertDoesNotThrow(() -> userService.login(new UserData("Bob", "12345", "bob@gmail.com")));
    }

    @Test
    public void loginIncorrectPassword() throws DataAccessException {
        userService.register(new UserData("Bob", "12345", "bob@gmail.com"));
        assertThrows(DataAccessException.class, () -> userService.login(new UserData("Bob", "67890", "bob@gmail.com")));

    }

    @Test
    public void loginInvalidUser() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> userService.login(new UserData("Bob", "12345", "bob@gmail.com")));
    }

    @Test
    public void logout() throws DataAccessException {
        UserData user = new UserData("Bob", "12345", "bob@gmail.com");
        String authToken = userService.register(user).authToken();
        assertDoesNotThrow(() -> userService.logout(authToken));
        assertNull(userDAO.getAuth(authToken));
    }

    @Test
    public void logoutInvalidAuth() throws DataAccessException {
        UserData user = new UserData("Bob", "12345", "bob@gmail.com");
        userService.register(user);
        String authToken = UUID.randomUUID().toString();
        assertThrows(DataAccessException.class, () -> userService.logout(authToken));
    }

}