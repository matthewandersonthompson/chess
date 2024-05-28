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
    void testRegisterFail() {
        UserData user = new UserData("testuser", "password", "email@example.com");
        try {
            userService.register(user);
            assertThrows(DataAccessException.class, () -> {
                userService.register(user); // This should throw an exception
            });
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
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
        try {
            userService.register(user);
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }

        assertThrows(DataAccessException.class, () -> {
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
}
