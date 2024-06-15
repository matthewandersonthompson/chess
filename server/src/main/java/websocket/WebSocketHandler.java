package websocket;

import com.google.gson.Gson;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/ws")
public class WebSocketHandler {

    private static final Map<Session, String> sessionUserMap = new HashMap<>();
    private static final Gson gson = new Gson();

    private final GameService gameService;
    private final AuthService authService;

    public WebSocketHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(session, (UserGameCommand.ConnectCommand) command);
                break;
            case MAKE_MOVE:
                handleMakeMove(session, (UserGameCommand.MakeMoveCommand) command);
                break;
            case LEAVE:
                handleLeave(session, (UserGameCommand.LeaveCommand) command);
                break;
            case RESIGN:
                handleResign(session, (UserGameCommand.ResignCommand) command);
                break;
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + session.getId());
        sessionUserMap.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void handleConnect(Session session, UserGameCommand.ConnectCommand command) {
        try {
            String username = authService.validateAuthToken(command.getAuthString());
            sessionUserMap.put(session, username);
            // Add additional logic for connecting to a game if needed
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            session.getAsyncRemote().sendText(gson.toJson(message));
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
            // Add logic to process the move
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            session.getAsyncRemote().sendText(gson.toJson(message));
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
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            session.getAsyncRemote().sendText(gson.toJson(message));
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
            // Add logic for resigning the game
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            session.getAsyncRemote().sendText(gson.toJson(message));
        } catch (Exception e) {
            sendErrorMessage(session, "Failed to resign: " + e.getMessage());
        }
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        message.setErrorMessage(errorMessage);
        session.getAsyncRemote().sendText(gson.toJson(message));
    }
}
