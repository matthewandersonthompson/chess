package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    private DataAccessInterface dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new InMemoryDataAccess();
    }

    @Test
    void testCreateAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");
        dataAccess.createAuth(auth);
        AuthData fetchedAuth = dataAccess.getAuth("token123");
        assertNotNull(fetchedAuth);
        assertEquals("user1", fetchedAuth.getUsername());
    }

    @Test
    void testCreateAuthFail() {
        AuthData auth = new AuthData("token123", "user1");
        assertThrows(DataAccessException.class, () -> {
            dataAccess.createAuth(auth);
            dataAccess.createAuth(auth);
        });
    }

    @Test
    void testGetAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");
        dataAccess.createAuth(auth);
        AuthData fetchedAuth = dataAccess.getAuth("token123");
        assertNotNull(fetchedAuth);
    }

    @Test
    void testGetAuthFail() {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getAuth("nonexistent");
        });
    }

    @Test
    void testDeleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");
        dataAccess.createAuth(auth);
        dataAccess.deleteAuth("token123");
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getAuth("token123");
        });
    }

    @Test
    void testDeleteAuthFail() {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.deleteAuth("nonexistent");
        });
    }

    @Test
    void testClearSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user1");
        dataAccess.createAuth(auth);
        dataAccess.clear();
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getAuth("token123");
        });
    }
}
