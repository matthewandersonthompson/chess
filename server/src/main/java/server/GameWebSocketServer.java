package server;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/ws")
public class GameWebSocketServer {
    private static final Map<Session, String> sessionAuthTokens = new HashMap<>();
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
        sendNotification(session, "User connected to game: " + command.getGameID());
    }

    private void handleMakeMoveCommand(Session session, UserGameCommand.MakeMoveCommand command) {
        // Handle the move logic here
        sendNotification(session, "Move made in game: " + command.getGameID());
    }

    private void handleLeaveCommand(Session session, UserGameCommand.LeaveCommand command) {
        sendNotification(session, "User left the game: " + command.getGameID());
    }

    private void handleResignCommand(Session session, UserGameCommand.ResignCommand command) {
        sendNotification(session, "User resigned from the game: " + command.getGameID());
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
}
