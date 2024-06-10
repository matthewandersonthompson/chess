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
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        try {
            facade.logout(); // Clear session data
            // Add any other necessary cleanup code
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
}
