package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException; // Add this import
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WebSocketHandler {

    private static final Map<Session, String> sessionUserMap = new HashMap<>();
    private static final Gson gson = new Gson();

    private static GameService gameService;
    private static AuthService authService;

    public WebSocketHandler() {
        // Default constructor needed for @WebSocket
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
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        try {
            validateCommand(command);
        } catch (Exception e) {
            sendErrorMessage(session, e.getMessage());
            return;
        }

        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(session, gson.fromJson(message, UserGameCommand.ConnectCommand.class));
                break;
            case MAKE_MOVE:
                handleMakeMove(session, gson.fromJson(message, UserGameCommand.MakeMoveCommand.class));
                break;
            case LEAVE:
                handleLeave(session, gson.fromJson(message, UserGameCommand.LeaveCommand.class));
                break;
            case RESIGN:
                handleResign(session, gson.fromJson(message, UserGameCommand.ResignCommand.class));
                break;
            default:
                sendErrorMessage(session, "Unknown command type.");
                break;
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + session.getRemoteAddress().getAddress());
        sessionUserMap.remove(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void validateCommand(UserGameCommand command) throws Exception {
        String username = authService.validateAuthToken(command.getAuthString());
        if (username == null) {
            throw new Exception("Invalid auth token.");
        }

        switch (command.getCommandType()) {
            case CONNECT:
            case MAKE_MOVE:
            case LEAVE:
            case RESIGN:
                if (command instanceof UserGameCommand.ConnectCommand) {
                    int gameID = ((UserGameCommand.ConnectCommand) command).getGameID();
                    if (!gameService.isValidGameID(gameID)) {
                        throw new Exception("Invalid game ID.");
                    }
                }
                break;
            default:
                throw new Exception("Unknown command type.");
        }
    }

    private void handleConnect(Session session, UserGameCommand.ConnectCommand command) {
        try {
            String username = authService.validateAuthToken(command.getAuthString());
            if (!gameService.isValidGameID(command.getGameID())) {
                sendErrorMessage(session, "Invalid game ID.");
                return;
            }
            sessionUserMap.put(session, username);
            // Add additional logic for connecting to a game if needed
            ChessGame game = gameService.loadGame(command.getGameID());
            ServerMessage message = new ServerMessage.LoadGameMessage(game);
            sendMessage(session, gson.toJson(message));
            broadcastNotificationExceptSender(session, username + " connected to the game.");
        } catch (Exception e) {
            sendErrorMessage(session, "Failed to connect: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, UserGameCommand.MakeMoveCommand command) {
        try {
            String username = sessionUserMap.get(session);
            if (username == null) {
                sendErrorMessage(session, "User not authenticated.");
                return;
            }

            ChessGame game = gameService.loadGame(command.getGameID());
            if (game.getTeamTurn() != gameService.getPlayerTeam(command.getGameID(), username)) {
                sendErrorMessage(session, "It's not your turn.");
                return;
            }

            // Check if the game is over before processing the move
            String gameStatus = gameService.checkForCheckAndCheckmate(game, game.getTeamTurn());
            if (!gameStatus.equals("none")) {
                sendErrorMessage(session, "Cannot make a move: game is over.");
                return;
            }

            // Process the move and get the updated game state
            game = gameService.processMove(command.getGameID(), command.getMove());

            // Send updated game state back to the player who made the move
            ServerMessage loadGameMessage = new ServerMessage.LoadGameMessage(game);
            sendMessage(session, gson.toJson(loadGameMessage));

            // Send updated game state and notification about the move to all other clients
            String moveDescription = String.format("%s moved from %s to %s", username, command.getMove().getStartPosition(), command.getMove().getEndPosition());
            broadcastMessageToAllExceptSender(session, gson.toJson(loadGameMessage));
            broadcastNotificationExceptSender(session, moveDescription);
        } catch (Exception e) {
            sendErrorMessage(session, "Failed to make move: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, UserGameCommand.LeaveCommand command) {
        try {
            String username = sessionUserMap.get(session);
            if (username == null) {
                sendErrorMessage(session, "User not authenticated.");
                return;
            }
            // Add logic for leaving the game
            sessionUserMap.remove(session);
            broadcastNotification(session, username + " left the game.");
        } catch (Exception e) {
            sendErrorMessage(session, "Failed to leave game: " + e.getMessage());
        }
    }

    private void handleResign(Session session, UserGameCommand.ResignCommand command) {
        try {
            String username = sessionUserMap.get(session);
            if (username == null) {
                sendErrorMessage(session, "User not authenticated.");
                return;
            }

            ChessGame game = gameService.loadGame(command.getGameID());

            // Check if the user is part of the game and if the game is already over
            ChessGame.TeamColor playerTeam;
            try {
                playerTeam = gameService.getPlayerTeam(command.getGameID(), username);
            } catch (DataAccessException e) {
                sendErrorMessage(session, "User not part of this game.");
                return;
            }

            String gameStatus = gameService.checkForCheckAndCheckmate(game, playerTeam);
            if (!gameStatus.equals("none")) {
                sendErrorMessage(session, "Cannot resign: game is already over.");
                return;
            }

            // Process the resign action
            broadcastNotification(session, username + " resigned from the game.");
            // Additional logic to handle the end of the game due to resign can be added here

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
        ServerMessage message = new ServerMessage.ErrorMessage(errorMessage);
        sendMessage(session, gson.toJson(message));
    }

    private void broadcastNotification(Session sender, String notification) {
        ServerMessage message = new ServerMessage.NotificationMessage(notification);
        for (Session session : sessionUserMap.keySet()) {
            if (session.isOpen()) {
                sendMessage(session, gson.toJson(message));
            }
        }
    }

    private void broadcastNotificationExceptSender(Session sender, String notification) {
        ServerMessage message = new ServerMessage.NotificationMessage(notification);
        for (Session session : sessionUserMap.keySet()) {
            if (session.isOpen() && !session.equals(sender)) {
                sendMessage(session, gson.toJson(message));
            }
        }
    }

    private void broadcastMessageToAllExceptSender(Session sender, String message) {
        for (Session session : sessionUserMap.keySet()) {
            if (session.isOpen() && !session.equals(sender)) {
                sendMessage(session, message);
            }
        }
    }
}
