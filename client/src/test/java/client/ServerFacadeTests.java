package client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Process serverProcess;
    private static ServerFacade facade;
    private static String adminAuthToken;

    private static final String serverHost = "localhost";
    private static final int serverPort = 8080;

    @BeforeAll
    public static void init() throws Exception {
        serverProcess = startServer();
        Thread.sleep(5000);

        facade = new ServerFacade(serverHost, serverPort);

        try {
            var adminRegisterResult = facade.register("admin", "adminpass", "admin@email.com");
            adminAuthToken = adminRegisterResult.authToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {
        serverProcess.destroy();
    }

    @BeforeEach
    public void clearDatabase() {
        try {
            sendClearDatabaseRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendClearDatabaseRequest() throws Exception {
        URL url = new URL("http://" + serverHost + ":" + serverPort + "/db");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP error code: " + responseCode);
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
            facade.register("", "password", "p1@email.com");
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
        facade.logout();
    }

    @Test
    public void createGameSuccess() throws Exception {
        facade.register("player4", "password", "p4@email.com");
        facade.login("player4", "password");
        var gameData = facade.createGame("TestGame");
        assertNotNull(gameData);
        assertTrue(gameData.gameID() > 0);
    }

    @Test
    public void createGameFailure() {
        assertThrows(Exception.class, () -> {
            facade.createGame("");
        });
    }

    @Test
    public void joinGameSuccess() throws Exception {
        facade.register("player5", "password", "p5@email.com");
        facade.login("player5", "password");
        var gameData = facade.createGame("TestGame");
        assertNotNull(gameData);
        facade.joinGame(gameData.gameID(), "White");
    }

    @Test
    public void joinGameFailure() {
        assertThrows(Exception.class, () -> {
            facade.joinGame(9999, "White");
        });
    }

    @Test
    public void listGamesSuccess() throws Exception {
        facade.register("player7", "password", "p7@email.com");
        facade.login("player7", "password");
        facade.createGame("TestGame");
        var games = facade.listGames();
        assertNotNull(games);
        assertTrue(games.games().size() > 0);
    }

    @Test
    public void listGamesFailure() {
        assertThrows(Exception.class, () -> {
            facade.listGames();
        });
    }

    private static Process startServer() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-cp", "out/production/server:out/production/shared", "Main"
        );
        return processBuilder.start();
    }
}
