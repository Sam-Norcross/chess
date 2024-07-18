package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void init() {
        userService = new UserService(new MemoryUserDAO());
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

}