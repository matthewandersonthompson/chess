package service;

import dataaccess.DataAccessException;
import dataaccess.InMemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;
    private InMemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new InMemoryDataAccess();
        authService = new AuthService(dataAccess);
    }

    @Test
    void testValidateAuthTokenSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");
        dataAccess.createAuth(auth);
        String username = authService.validateAuthToken("token123");
        assertEquals("user1", username);
    }

    @Test
    void testValidateAuthTokenInvalid() {
        assertThrows(DataAccessException.class, () -> {
            authService.validateAuthToken("invalidToken");
        });
    }

    @Test
    void testCreateAuthToken() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");
        authService.createAuth(auth);
        AuthData retrievedAuth = dataAccess.getAuth("token123");
        assertEquals(auth, retrievedAuth);
    }

    @Test
    void testDeleteAuthToken() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");
        dataAccess.createAuth(auth);
        authService.deleteAuth("token123");
        assertThrows(DataAccessException.class, () -> dataAccess.getAuth("token123"));
    }
}
