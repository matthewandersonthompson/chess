package service;

import dataaccess.DataAccessException;
import dataaccess.InMemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService gameService;
    private InMemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new InMemoryDataAccess();
        gameService = new GameService(dataAccess);
    }

    @Test
    void testCreateGameSuccess() {
        try {
            GameData game = gameService.createGame("Test Game");
            assertNotNull(game);
            assertEquals("Test Game", game.getGameName());
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testJoinGameSuccess() {
        try {
            GameData game = gameService.createGame("Test Game");
            gameService.joinGame(game.getGameID(), "user1", "WHITE");
            GameData updatedGame = dataAccess.getGame(game.getGameID());
            assertEquals("user1", updatedGame.getWhiteUsername());
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testJoinGameInvalidColor() {
        GameData game = null;
        try {
            game = gameService.createGame("Test Game");
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }

        GameData finalGame = game;
        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(finalGame.getGameID(), "user1", "INVALID");
        });
    }

    @Test
    void testListGamesSuccess() {
        try {
            gameService.createGame("Test Game 1");
            gameService.createGame("Test Game 2");
            List<GameData> games = gameService.listGames();
            assertEquals(2, games.size());
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testClearSuccess() {
        try {
            gameService.createGame("Test Game");
            gameService.clear();
            List<GameData> games = gameService.listGames();
            assertTrue(games.isEmpty());
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testCreateGameFail() {
        try {
            dataAccess.createGame(new GameData(1, null, null, "Existing Game", null));
            assertThrows(DataAccessException.class, () -> {
                gameService.createGame("Existing Game");
            });
        } catch (DataAccessException e) {
            fail("Exception should not be thrown during setup: " + e.getMessage());
        }
    }

    @Test
    void testJoinGameUserAlreadyExists() {
        try {
            GameData game = gameService.createGame("Test Game");
            gameService.joinGame(game.getGameID(), "user1", "WHITE");
            assertThrows(DataAccessException.class, () -> {
                gameService.joinGame(game.getGameID(), "user2", "WHITE");
            });
        } catch (DataAccessException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
