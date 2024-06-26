package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.AuthService;
import service.GameService;
import websocket.commands.*;
import websocket.messages.*;
import websocket.messages.Error;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private static final Map<Session, String> SESSION_USER_MAP = new HashMap<>();
    private static final Map<String, Integer> USER_GAME_MAP = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();

    private static GameService gameService;
    private static AuthService authService;

    public WebSocketHandler() {
    }

    public static void setServices(GameService gameService, AuthService authService) {
        WebSocketHandler.gameService = gameService;
        WebSocketHandler.authService = authService;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connection opened: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Received message: " + message);
        UserGameCommand command = GSON.fromJson(message, UserGameCommand.class);

        try {
            validateCommand(command);
        } catch (Exception e) {
            sendErrorMessage(session, e.getMessage());
            return;
        }

        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(session, GSON.fromJson(message, Connect.class));
                break;
            case MAKE_MOVE:
                handleMakeMove(session, GSON.fromJson(message, MakeMove.class));
                break;
            case LEAVE:
                handleLeave(session, GSON.fromJson(message, Leave.class));
                break;
            case RESIGN:
                handleResign(session, GSON.fromJson(message, Resign.class));
                break;
            default:
                sendErrorMessage(session, "Unknown command type.");
                break;
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + session.getRemoteAddress().getAddress());
        String username = SESSION_USER_MAP.remove(session);
        USER_GAME_MAP.remove(username);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void validateCommand(UserGameCommand command) throws Exception {
        String username = authService.validateAuthToken(command.getAuthToken());
        if (username == null) {
            throw new Exception("Invalid auth token.");
        }

        switch (command.getCommandType()) {
            case CONNECT:
            case MAKE_MOVE:
            case LEAVE:
            case RESIGN:
                if (command instanceof Connect) {
                    int gameID = ((Connect) command).getGameID();
                    if (!gameService.isValidGameID(gameID)) {
                        throw new Exception("Invalid game ID.");
                    }
                }
                break;
            default:
                throw new Exception("Unknown command type.");
        }
    }

    private void handleConnect(Session session, Connect command) {
        try {
            String username = authService.validateAuthToken(command.getAuthToken());
            if (!gameService.isValidGameID(command.getGameID())) {
                sendErrorMessage(session, "Invalid game ID.");
                return;
            }
            SESSION_USER_MAP.put(session, username);
            USER_GAME_MAP.put(username, command.getGameID());
            ChessGame game = gameService.loadGame(command.getGameID());
            LoadGame message = new LoadGame(game);
            sendMessage(session, GSON.toJson(message));
            broadcastNotificationExceptSender(session, username + " connected to the game.", command.getGameID());
        } catch (Exception e) {
            sendErrorMessage(session, "Failed to connect: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, MakeMove command) {
        try {
            String username = SESSION_USER_MAP.get(session);
            if (username == null) {
                sendErrorMessage(session, "User not authenticated.");
                return;
            }

            ChessGame game = gameService.loadGame(command.getGameID());
            if (game.getTeamTurn() != gameService.getPlayerTeam(command.getGameID(), username)) {
                sendErrorMessage(session, "It's not your turn.");
                return;
            }

            if (game.isGameOver()) {
                sendErrorMessage(session, "Cannot make a move: game is over.");
                return;
            }

            game = gameService.processMove(command.getGameID(), command.getMove());

            LoadGame loadGameMessage = new LoadGame(game);
            sendMessage(session, GSON.toJson(loadGameMessage));

            String moveDescription = String.format("%s moved from %s to %s", username, command.getMove().getStartPosition(), command.getMove().getEndPosition());
            broadcastMessageToAllExceptSender(session, GSON.toJson(loadGameMessage), command.getGameID());
            broadcastNotificationExceptSender(session, moveDescription, command.getGameID());
        } catch (Exception e) {
            sendErrorMessage(session, "Failed to make move: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, Leave command) {
        try {
            String username = SESSION_USER_MAP.get(session);
            if (username == null) {
                sendErrorMessage(session, "User not authenticated.");
                return;
            }

            SESSION_USER_MAP.remove(session);
            Integer gameID = USER_GAME_MAP.remove(username);

            if (gameID == null || gameID != command.getGameID()) {
                sendErrorMessage(session, "User not part of this game.");
                return;
            }

            gameService.removePlayer(command.getGameID(), username);
            broadcastNotificationExceptSender(session, username + " left the game.", command.getGameID());
        } catch (Exception e) {
            sendErrorMessage(session, "Failed to leave game: " + e.getMessage());
        }
    }

    private void handleResign(Session session, Resign command) {
        try {
            String username = SESSION_USER_MAP.get(session);
            if (username == null) {
                sendErrorMessage(session, "User not authenticated.");
                return;
            }

            ChessGame game = gameService.loadGame(command.getGameID());
            System.out.println("Handling resign for user: " + username + ", gameID: " + command.getGameID());

            ChessGame.TeamColor playerTeam;
            try {
                playerTeam = gameService.getPlayerTeam(command.getGameID(), username);
            } catch (DataAccessException e) {
                sendErrorMessage(session, "User not part of this game.");
                return;
            }

            if (game.isGameOver()) {
                System.out.println("Game is already over. Sending error message.");
                sendErrorMessage(session, "Cannot resign: game is already over.");
                return;
            }

            game.setGameOver(true);
            gameService.saveGame(command.getGameID(), game);
            System.out.println("User " + username + " resigned. Game set to over.");
            broadcastNotification(session, username + " resigned from the game.", command.getGameID());
        } catch (Exception e) {
            sendErrorMessage(session, "Failed to resign: " + e.getMessage());
        }
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        System.out.println("Sending error message: " + errorMessage);
        Error message = new Error(errorMessage);
        sendMessage(session, GSON.toJson(message));
    }

    private void broadcastNotification(Session sender, String notification, int gameID) {
        Notification message = new Notification(notification);
        for (Session session : SESSION_USER_MAP.keySet()) {
            String username = SESSION_USER_MAP.get(session);
            if (session.isOpen() && USER_GAME_MAP.get(username) == gameID) {
                sendMessage(session, GSON.toJson(message));
            }
        }
    }

    private void broadcastNotificationExceptSender(Session sender, String notification, int gameID) {
        Notification message = new Notification(notification);
        for (Session session : SESSION_USER_MAP.keySet()) {
            String username = SESSION_USER_MAP.get(session);
            if (session.isOpen() && !session.equals(sender) && USER_GAME_MAP.get(username) == gameID) {
                sendMessage(session, GSON.toJson(message));
            }
        }
    }

    private void broadcastMessageToAllExceptSender(Session sender, String message, int gameID) {
        for (Session session : SESSION_USER_MAP.keySet()) {
            String username = SESSION_USER_MAP.get(session);
            if (session.isOpen() && !session.equals(sender) && USER_GAME_MAP.get(username) == gameID) {
                sendMessage(session, message);
            }
        }
    }
}
