package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import requests.LoginRequest;
import requests.RegisterRequest;
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
}
