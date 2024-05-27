package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import results.CreateGameResult;
import results.JoinGameResult;
import results.ListGamesResult;
import service.GameService;
import service.AuthService;
import spark.Request;
import spark.Response;
import spark.Route;

public class GameHandler {
    private GameService gameService;
    private AuthService authService;
    private final Gson gson = new Gson();

    // Constructor to initialize gameService and authService
    public GameHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    public Route handleCreateGame = (Request req, Response res) -> {
        String authToken = req.headers("authorization");
        CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);
        try {
            authService.validateAuthToken(authToken);
            var game = gameService.createGame(request.gameName());
            res.status(200);
            return gson.toJson(new CreateGameResult(game.gameID()));
        } catch (DataAccessException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };

    public Route handleListGames = (Request req, Response res) -> {
        String authToken = req.headers("authorization");
        try {
            authService.validateAuthToken(authToken);
            var games = gameService.listGames();
            res.status(200);
            return gson.toJson(new ListGamesResult(games));
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };

    public Route handleJoinGame = (Request req, Response res) -> {
        String authToken = req.headers("authorization");
        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
        try {
            String username = authService.validateAuthToken(authToken);
            gameService.joinGame(request.gameID(), username, request.playerColor());
            res.status(200);
            return gson.toJson(new JoinGameResult());
        } catch (DataAccessException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };
}
