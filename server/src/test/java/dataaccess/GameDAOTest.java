package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {
    private DataAccessInterface dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new InMemoryDataAccess();
    }

    @Test
    void testCreateGameSuccess() throws DataAccessException {
        GameData game = new GameData(0, "Test Game", 0, 0, "gameState");
        dataAccess.createGame(game);
        GameData fetchedGame = dataAccess.getGame(game.getGameID());
        assertNotNull(fetchedGame);
        assertEquals("Test Game", fetchedGame.getGameName());
    }

    @Test
    void testCreateGameFail() {
        GameData game = new GameData(0, "Test Game", 0, 0, "gameState");
        try {
            dataAccess.createGame(game);
            assertThrows(DataAccessException.class, () -> {
                dataAccess.createGame(game); // Should fail due to duplicate game ID
            });
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testGetGameSuccess() throws DataAccessException {
        GameData game = new GameData(0, "Test Game", 0, 0, "gameState");
        dataAccess.createGame(game);
        GameData fetchedGame = dataAccess.getGame(game.getGameID());
        assertNotNull(fetchedGame);
    }

    @Test
    void testGetGameFail() {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getGame(999);
        });
    }

    @Test
    void testClearSuccess() throws DataAccessException {
        GameData game = new GameData(0, "Test Game", 0, 0, "gameState");
        dataAccess.createGame(game);
        dataAccess.clear();
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getGame(game.getGameID());
        });
    }
}
