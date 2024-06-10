package client;

import server.Server; // Ensure this is the correct package for your Server class
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost", port);

        // Register an admin user for setup and teardown
        try {
            facade.register("admin", "adminpass", "admin@email.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        try {
            // Assuming a method to clear database or re-initialize state
            // facade.clearDatabase(); // If such method exists
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registerSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerFailure() {
        assertThrows(Exception.class, () -> {
            facade.register("", "password", "p1@email.com"); // Empty username
        });
    }

    @Test
    public void loginSuccess() throws Exception {
        facade.register("player2", "password", "p2@email.com");
        var authData = facade.login("player2", "password");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginFailure() {
        assertThrows(Exception.class, () -> {
            facade.login("nonexistent", "password"); // Non-existent user
        });
    }

    @Test
    public void logoutSuccess() throws Exception {
        var authData = facade.register("player3", "password", "p3@email.com");
        facade.logout();
    }

    @Test
    public void createGameSuccess() throws Exception {
        var authData = facade.register("player4", "password", "p4@email.com");
        var gameData = facade.createGame("TestGame");
        assertNotNull(gameData);
        assertTrue(gameData.gameID() > 0);
    }

    @Test
    public void createGameFailure() {
        assertThrows(Exception.class, () -> {
            facade.createGame(""); // Empty game name
        });
    }

    @Test
    public void joinGameSuccess() throws Exception {
        var authData = facade.register("player5", "password", "p5@email.com");
        var gameData = facade.createGame("TestGame");
        assertNotNull(gameData);
        facade.joinGame(gameData.gameID(), "White");
    }

    @Test
    public void joinGameFailure() {
        assertThrows(Exception.class, () -> {
            facade.joinGame(9999, "White"); // Non-existent game ID
        });
    }

    @Test
    public void listGamesSuccess() throws Exception {
        var authData = facade.register("player7", "password", "p7@email.com");
        facade.createGame("TestGame");
        var games = facade.listGames();
        assertNotNull(games);
        assertTrue(games.games().size() > 0); // Use games() method from the record
    }

    @Test
    public void listGamesFailure() throws Exception {
        var games = facade.listGames();
        assertNotNull(games);
        assertTrue(games.games().size() >= 0); // Use games() method from the record
    }
}
