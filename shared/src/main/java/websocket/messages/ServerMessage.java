package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage))
            return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    // ****************** END OF PRE-EXISTING METHODS ******************

    // Added code starts here

    private String message;  // Added field
    private String errorMessage;  // Added field

    public ServerMessage(ServerMessageType type, String message) { // Added constructor
        this.serverMessageType = type;
        this.message = message;
    }

    public String getMessage() {  // Added method
        return this.message;
    }

    public void setMessage(String message) {  // Added method
        this.message = message;
    }

    public String getErrorMessage() {  // Added method
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {  // Added method
        this.errorMessage = errorMessage;
    }

    // Subclasses

    public static class LoadGameMessage extends ServerMessage {
        private final ChessGame game;

        public LoadGameMessage(ChessGame game) {
            super(ServerMessageType.LOAD_GAME);
            this.game = game;
        }

        public ChessGame getGame() {
            return game;
        }
    }

    public static class ErrorMessage extends ServerMessage {
        private final String error;

        public ErrorMessage(String error) {
            super(ServerMessageType.ERROR);
            this.error = error;
            this.setErrorMessage(error); // Ensure the errorMessage field is set
        }

        public String getError() {
            return error;
        }

        @Override
        public String getErrorMessage() {
            return error;
        }
    }

    public static class NotificationMessage extends ServerMessage {
        public NotificationMessage(String message) {
            super(ServerMessageType.NOTIFICATION, message);
        }
    }
}
