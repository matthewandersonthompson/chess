package server;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import chess.ChessGame;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/ws")
public class GameWebSocketServer {
    private static final Map<Session, String> sessionAuthTokens = new HashMap<>();
    private static final Map<Integer, ChessGame> gameStates = new HashMap<>(); // Add game states map
    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received: " + message);
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        handleCommand(session, command);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket connection closed: " + session.getId() + " Reason: " + reason);
        sessionAuthTokens.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void handleCommand(Session session, UserGameCommand command) {
        // Handle the command based on its type
        switch (command.getCommandType()) {
            case CONNECT:
                handleConnectCommand(session, (UserGameCommand.ConnectCommand) command);
                break;
            case MAKE_MOVE:
                handleMakeMoveCommand(session, (UserGameCommand.MakeMoveCommand) command);
                break;
            case LEAVE:
                handleLeaveCommand(session, (UserGameCommand.LeaveCommand) command);
                break;
            case RESIGN:
                handleResignCommand(session, (UserGameCommand.ResignCommand) command);
                break;
            default:
                sendErrorMessage(session, "Unknown command type: " + command.getCommandType());
        }
    }

    private void handleConnectCommand(Session session, UserGameCommand.ConnectCommand command) {
        sessionAuthTokens.put(session, command.getAuthString());
        gameStates.putIfAbsent(command.getGameID(), new ChessGame()); // Initialize game if not present
        sendNotification(session, "User connected to game: " + command.getGameID());
    }

    private void handleMakeMoveCommand(Session session, UserGameCommand.MakeMoveCommand command) {
        ChessGame game = gameStates.get(command.getGameID());
        if (game == null) {
            sendErrorMessage(session, "Game not found: " + command.getGameID());
            return;
        }

        try {
            game.makeMove(command.getMove());
            broadcastMessageToGame(command.getGameID(), new ServerMessage.NotificationMessage("Move made in game: " + command.getGameID()));
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid move: " + e.getMessage());
        }
    }

    private void handleLeaveCommand(Session session, UserGameCommand.LeaveCommand command) {
        sendNotification(session, "User left the game: " + command.getGameID());
        // Handle additional leave logic here
    }

    private void handleResignCommand(Session session, UserGameCommand.ResignCommand command) {
        sendNotification(session, "User resigned from the game: " + command.getGameID());
        // Handle additional resign logic here
    }

    private void sendNotification(Session session, String message) {
        try {
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(message);
            session.getBasicRemote().sendText(gson.toJson(notification));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage(errorMessage);
            session.getBasicRemote().sendText(gson.toJson(error));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessageToGame(int gameID, ServerMessage message) {
        sessionAuthTokens.keySet().stream()
                .filter(session -> gameStates.containsKey(gameID))
                .forEach(session -> {
                    try {
                        session.getBasicRemote().sendText(gson.toJson(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
