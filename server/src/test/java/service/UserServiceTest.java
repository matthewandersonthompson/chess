package service;

import dataaccess.DataAccessException;
import dataaccess.InMemoryDataAccess;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private InMemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new InMemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    @Test
    void testRegisterSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password", "email@example.com");
        AuthData auth = userService.register(user);
        assertNotNull(auth);
        assertEquals("testuser", auth.username());
    }

    @Test
    void testRegisterUserAlreadyExists() {
        UserData user = new UserData("testuser", "password", "email@example.com");
        assertThrows(DataAccessException.class, () -> {
            userService.register(user);
            userService.register(user);
        });
    }

    @Test
    void testLoginSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password", "email@example.com");
        userService.register(user);
        AuthData auth = userService.login("testuser", "password");
        assertNotNull(auth);
        assertEquals("testuser", auth.username());
    }

    @Test
    void testLoginInvalidPassword() {
        UserData user = new UserData("testuser", "password", "email@example.com");
        assertThrows(DataAccessException.class, () -> {
            userService.register(user);
            userService.login("testuser", "wrongpassword");
        });
    }

    @Test
    void testLogoutSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password", "email@example.com");
        AuthData auth = userService.register(user);
        userService.logout(auth.authToken());
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getAuth(auth.authToken());
        });
    }

    @Test
    void testRegisterFail() {
        assertThrows(DataAccessException.class, () -> {
            userService.register(new UserData(null, "password", "email@example.com"));
        });
        assertThrows(DataAccessException.class, () -> {
            userService.register(new UserData("testuser", null, "email@example.com"));
        });
        assertThrows(DataAccessException.class, () -> {
            userService.register(new UserData("testuser", "password", null));
        });
    }

    @Test
    void testLoginFail() {
        assertThrows(DataAccessException.class, () -> {
            userService.login("nonexistentuser", "password");
        });
    }
}
