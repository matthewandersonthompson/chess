package server;

import com.google.gson.Gson;
import dataaccess.InMemoryDataAccess;
import service.AuthService;
import service.GameService;
import service.UserService;
import handlers.ClearHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");

        Gson gson = new Gson();

        var dataAccess = new InMemoryDataAccess();

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
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
