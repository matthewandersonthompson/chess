package server;

import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.MySQLDataAccess;
import handlers.*;
import service.*;
import spark.Spark;
import websocket.WebSocketHandler;

public class Server {

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");

        Gson gson = new Gson();

        var dataAccess = new MySQLDataAccess();

        var userService = new UserService(dataAccess);
        var gameService = new GameService(dataAccess);
        var authService = new AuthService(dataAccess);

        var clearHandler = new ClearHandler(gameService);
        var userHandler = new UserHandler(userService);
        var gameHandler = new GameHandler(gameService, authService);

        WebSocketHandler.setServices(gameService, authService);

        // Configure WebSocket before HTTP routes
        Spark.webSocket("/ws", WebSocketHandler.class);

        Spark.delete("/db", clearHandler);
        Spark.post("/user", userHandler.handleRegister);
        Spark.post("/session", userHandler.handleLogin);
        Spark.delete("/session", userHandler.handleLogout);
        Spark.post("/game", gameHandler.handleCreateGame);
        Spark.get("/game", gameHandler.handleListGames);
        Spark.put("/game", gameHandler.handleJoinGame);

        Spark.init();

        Spark.awaitInitialization();
        int actualPort = Spark.port();
        System.out.println("Started test HTTP server on " + actualPort);  // Print the port number
        return actualPort;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
