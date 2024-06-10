package server;

import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.MySQLDataAccess;
import service.AuthService;
import service.GameService;
import service.UserService;
import handlers.ClearHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import spark.Spark;

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

        Spark.delete("/db", clearHandler);
        Spark.post("/user", userHandler.handleRegister);
        Spark.post("/session", userHandler.handleLogin);
        Spark.delete("/session", userHandler.handleLogout);
        Spark.post("/game", gameHandler.handleCreateGame);
        Spark.get("/game", gameHandler.handleListGames);
        Spark.put("/game", gameHandler.handleJoinGame);

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
