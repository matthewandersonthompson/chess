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
        GameData game = new GameData(0, "Test Game", null, null, "gameState");
        dataAccess.createGame(game);
        GameData fetchedGame = dataAccess.getGame(game.getGameID());
        assertNotNull(fetchedGame);
        assertEquals("Test Game", fetchedGame.getGameName());
    }

    @Test
    void testCreateGameFail() {
        GameData game1 = new GameData(0, "Test Game", null, null, "gameState");
        GameData game2 = new GameData(0, "Test Game", null, null, "gameState");
        assertThrows(DataAccessException.class, () -> {
            dataAccess.createGame(game1);
            dataAccess.createGame(game2);
        });
    }

    @Test
    void testGetGameSuccess() throws DataAccessException {
        GameData game = new GameData(0, "Test Game", null, null, "gameState");
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
        GameData game = new GameData(0, "Test Game", null, null, "gameState");
        dataAccess.createGame(game);
        dataAccess.clear();
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getGame(game.getGameID());
        });
    }

    @Test
    void testUpdateGameSuccess() throws DataAccessException {
        GameData game = new GameData(0, "Test Game", null, null, "gameState");
        dataAccess.createGame(game);
        game.setGameState("newState");
        dataAccess.updateGame(game);
        GameData updatedGame = dataAccess.getGame(game.getGameID());
        assertEquals("newState", updatedGame.getGameState());
    }

    @Test
    void testUpdateGameFail() {
        GameData game = new GameData(999, "Non-existent Game", null, null, "gameState");
        assertThrows(DataAccessException.class, () -> {
            dataAccess.updateGame(game);
        });
    }
}
