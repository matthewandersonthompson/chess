package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private DataAccessInterface dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new InMemoryDataAccess();
    }

    @Test
    void testCreateUserSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password", "email@example.com");
        dataAccess.createUser(user);
        UserData fetchedUser = dataAccess.getUser("testuser");
        assertNotNull(fetchedUser);
        assertEquals("testuser", fetchedUser.getUsername());
    }

    @Test
    void testCreateUserFail() {
        UserData user = new UserData("testuser", "password", "email@example.com");
        try {
            dataAccess
                    .createUser(user);
            assertThrows(DataAccessException.class, () -> {
                dataAccess.createUser(user);
            });
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testGetUserSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password", "email@example.com");
        dataAccess.createUser(user);
        UserData fetchedUser = dataAccess.getUser("testuser");
        assertNotNull(fetchedUser);
    }

    @Test
    void testGetUserFail() {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getUser("nonexistent");
        });
    }

    @Test
    void testClearSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password", "email@example.com");
        dataAccess.createUser(user);
        dataAccess.clear();
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getUser("testuser");
        });
    }
}
