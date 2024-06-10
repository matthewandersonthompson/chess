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
    private static String adminToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost", port);

        // Register an admin user for setup and teardown
        try {
            var authData = facade.register("admin", "adminpass", "admin@email.com");
            adminToken = authData.authToken();
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
            // Use admin token to clear session data
            if (adminToken != null) {
                facade.setAuthToken(adminToken);
                facade.logout();
            }
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
            facade.login("nonexistent", "password");
        });
    }

    @Test
    public void logoutSuccess() throws Exception {
        var authData = facade.register("player3", "password", "p3@email.com");
        facade.setAuthToken(authData.authToken());
        facade.logout();
        assertThrows(Exception.class, () -> {
            facade.logout(); // Should fail since user is already logged out
        });
    }

    @Test
    public void createGameSuccess() throws Exception {
        var authData = facade.register("player4", "password", "p4@email.com");
        facade.setAuthToken(authData.authToken());
        var gameData = facade.createGame();
        assertNotNull(gameData);
        assertTrue(gameData.gameId() > 0);
    }

    @Test
    public void joinGameSuccess() throws Exception {
        var authData = facade.register("player5", "password", "p5@email.com");
        facade.setAuthToken(authData.authToken());
        var gameData = facade.createGame();
        assertNotNull(gameData);
        facade.joinGame(gameData.gameId());
    }

    @Test
    public void joinGameFailure() throws Exception {
        var authData = facade.register("player6", "password", "p6@email.com");
        facade.setAuthToken(authData.authToken());
        assertThrows(Exception.class, () -> {
            facade.joinGame(-1); // Invalid game ID
        });
    }

    @Test
    public void listGamesSuccess() throws Exception {
        var authData = facade.register("player7", "password", "p7@email.com");
        facade.setAuthToken(authData.authToken());
        facade.createGame();
        var games = facade.listGames();
        assertNotNull(games);
        assertTrue(games.size() > 0);
    }

    @Test
    public void makeMoveSuccess() throws Exception {
        var authData = facade.register("player8", "password", "p8@email.com");
        facade.setAuthToken(authData.authToken());
        var gameData = facade.createGame();
        facade.joinGame(gameData.gameId());
        facade.makeMove(gameData.gameId(), "e2e4");
    }

    @Test
    public void makeMoveFailure() throws Exception {
        var authData = facade.register("player9", "password", "p9@email.com");
        facade.setAuthToken(authData.authToken());
        var gameData = facade.createGame();
        facade.joinGame(gameData.gameId());
        assertThrows(Exception.class, () -> {
            facade.makeMove(gameData.gameId(), "invalid_move");
        });
    }
}
