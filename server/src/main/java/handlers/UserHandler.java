package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.RegisterRequest;
import requests.LoginRequest;
import results.RegisterResult;
import results.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserHandler {
    private UserService userService;
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);

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
            var user = new model.UserData(request.username(), request.password(), request.email());
            var auth = userService.register(user);
            res.status(200);
            return gson.toJson(new RegisterResult(auth.getUsername(), auth.getAuthToken()));
        } catch (DataAccessException e) {
            res.status(403);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    };

    public Route handleLogin = (Request req, Response res) -> {
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        LOGGER.info("Login attempt for user: {}", request.username());
        try {
            var auth = userService.login(request.username(), request.password());
            res.status(200);
            return gson.toJson(new LoginResult(auth.getUsername(), auth.getAuthToken()));
        } catch (DataAccessException e) {
            LOGGER.error("Login failed for user: {} - {}", request.username(), e.getMessage());
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
