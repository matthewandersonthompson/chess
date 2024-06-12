package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import requests.MakeMoveRequest;
import requests.LeaveGameRequest;
import requests.ResignGameRequest;
import chess.ChessMove;
import results.CreateGameResult;
import results.ListGamesResult;
import results.LoginResult;
import results.RegisterResult;
import results.MakeMoveResult;
import websocket.messages.ServerMessage; // Import ServerMessage

import javax.websocket.*;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@ClientEndpoint
public class ServerFacade {
    private final String serverHost;
    private final int serverPort;
    private final Gson gson = new Gson();
    private String authToken; // Store the auth token here
    private Session session;

    public ServerFacade(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        initWebSocket();
    }

    private void initWebSocket() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI("ws://" + serverHost + ":" + serverPort + "/ws");
            container.connectToServer(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connection opened");
    }

    @OnMessage
    public void onMessage(String message) {
        handleWebSocketMessage(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void handleWebSocketMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                // Handle game load message
                break;
            case ERROR:
                // Handle error message
                break;
            case NOTIFICATION:
                System.out.println("Notification received: " + serverMessage.getMessage());
                break;
        }
    }

    public void sendWebSocketMessage(Object message) {
        String jsonMessage = gson.toJson(message);
        session.getAsyncRemote().sendText(jsonMessage);
    }

    private JsonObject sendRequest(String endpoint, String method, Object requestBody) throws Exception {
        URL url = new URL("http://" + serverHost + ":" + serverPort + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        if (authToken != null && !authToken.isEmpty()) {
            connection.setRequestProperty("Authorization", authToken);
        }
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
        RegisterResult result = gson.fromJson(response, RegisterResult.class);
        this.authToken = result.authToken(); // Store the auth token
        return result;
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        JsonObject response = sendRequest("/session", "POST", request);
        LoginResult result = gson.fromJson(response, LoginResult.class);
        this.authToken = result.authToken(); // Store the auth token
        return result;
    }

    public void logout() throws Exception {
        sendRequest("/session", "DELETE", null);
        this.authToken = null; // Clear the auth token
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

    // Add the makeMove method
    public MakeMoveResult makeMove(int gameID, ChessMove move) throws Exception {
        MakeMoveRequest request = new MakeMoveRequest(gameID, move);
        JsonObject response = sendRequest("/game/move", "POST", request);
        sendWebSocketMessage(request); // Send the move to the server via WebSocket
        return gson.fromJson(response, MakeMoveResult.class);
    }

    // Add the leaveGame method
    public void leaveGame(int gameID) throws Exception {
        LeaveGameRequest request = new LeaveGameRequest(gameID);
        sendRequest("/game/leave", "POST", request);
        sendWebSocketMessage(request); // Send the leave command to the server via WebSocket
    }

    // Add the resignGame method
    public void resignGame(int gameID) throws Exception {
        ResignGameRequest request = new ResignGameRequest(gameID);
        sendRequest("/game/resign", "POST", request);
        sendWebSocketMessage(request); // Send the resign command to the server via WebSocket
    }
}
