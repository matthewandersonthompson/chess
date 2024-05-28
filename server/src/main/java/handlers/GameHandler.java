package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import results.CreateGameResult;
import results.JoinGameResult;
import results.ListGamesResult;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class GameHandler {
    private GameService gameService;
    private AuthService authService;
    private final Gson gson = new Gson();

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
            return gson.toJson(new CreateGameResult(game.getGameID()));
        } catch (DataAccessException e) {
            res.status(e.getMessage().equals("Auth token not found") ? 401 : 400);
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
            res.status(e.getMessage().equals("Auth token not found") ? 401 : 400);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };

    public Route handleJoinGame = (Request req, Response res) -> {
        String authToken = req.headers("authorization");
        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
        try {
            String username = authService.validateAuthToken(authToken);
            gameService.joinGame(request.getGameId(), username, request.getPlayerColor());
            res.status(200);
            return gson.toJson(new JoinGameResult());
        } catch (DataAccessException e) {
            int status = switch (e.getMessage()) {
                case "Auth token not found" -> 401;
                case "Game not found" -> 400;
                case "White player already taken", "Black player already taken", "Invalid player color" -> 403;
                default -> 400;
            };
            res.status(status);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };
}
