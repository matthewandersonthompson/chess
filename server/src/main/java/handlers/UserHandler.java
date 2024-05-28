package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import requests.RegisterRequest;
import requests.LoginRequest;
import results.RegisterResult;
import results.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserHandler {
    private UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Route handleRegister = (Request req, Response res) -> {
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty() ||
                request.email() == null || request.email().isEmpty()) {
            res.status(400);
            return gson.toJson(new ErrorResponse("Error: Missing required fields"));
        }
        try {
            var user = new UserData(request.username(), request.password(), request.email());
            var auth = userService.register(user);
            res.status(200);
            return gson.toJson(new RegisterResult(auth.username(), auth.authToken()));
        } catch (DataAccessException e) {
            res.status(403);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };

    public Route handleLogin = (Request req, Response res) -> {
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        try {
            var auth = userService.login(request.username(), request.password());
            res.status(200);
            return gson.toJson(new LoginResult(auth.username(), auth.authToken()));
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };

    public Route handleLogout = (Request req, Response res) -> {
        String authToken = req.headers("authorization");
        try {
            userService.logout(authToken);
            res.status(200);
            return gson.toJson(new Object());
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };
}
