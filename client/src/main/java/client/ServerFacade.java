package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.ListGamesResult;
import results.LoginResult;
import results.RegisterResult;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private final String serverHost;
    private final int serverPort;
    private final Gson gson = new Gson();

    public ServerFacade(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    private JsonObject sendRequest(String endpoint, String method, Object requestBody) throws Exception {
        URL url = new URL("http://" + serverHost + ":" + serverPort + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (requestBody != null) {
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                gson.toJson(requestBody, writer);
            }
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (var reader = new java.io.InputStreamReader(connection.getInputStream())) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        } else {
            throw new Exception("HTTP error code: " + connection.getResponseCode());
        }
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);
        JsonObject response = sendRequest("/user", "POST", request);
        return gson.fromJson(response, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        JsonObject response = sendRequest("/session", "POST", request);
        return gson.fromJson(response, LoginResult.class);
    }

    public void logout() throws Exception {
        sendRequest("/session", "DELETE", null);
    }

    public CreateGameResult createGame(String gameName) throws Exception {
        CreateGameRequest request = new CreateGameRequest(gameName);
        JsonObject response = sendRequest("/game", "POST", request);
        return gson.fromJson(response, CreateGameResult.class);
    }

    public ListGamesResult listGames() throws Exception {
        JsonObject response = sendRequest("/game", "GET", null);
        return gson.fromJson(response, ListGamesResult.class);
    }

    public void joinGame(int gameID, String playerColor) throws Exception {
        JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
        sendRequest("/game", "PUT", request);
    }
}
