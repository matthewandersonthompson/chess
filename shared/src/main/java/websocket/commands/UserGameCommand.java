package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public UserGameCommand(String authToken) {
        this.authToken = authToken;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private final String authToken;

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }

    // ****************** END OF PRE-EXISTING METHODS ******************

    // Subclasses

    public static class ConnectCommand extends UserGameCommand {
        private final int gameID;

        public ConnectCommand(String authToken, int gameID) {
            super(authToken);
            this.commandType = CommandType.CONNECT;
            this.gameID = gameID;
        }

        public int getGameID() {
            return gameID;
        }
    }

    public static class MakeMoveCommand extends UserGameCommand {
        private final int gameID;
        private final ChessMove move;

        public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
            super(authToken);
            this.commandType = CommandType.MAKE_MOVE;
            this.gameID = gameID;
            this.move = move;
        }

        public int getGameID() {
            return gameID;
        }

        public ChessMove getMove() {
            return move;
        }
    }

    public static class LeaveCommand extends UserGameCommand {
        private final int gameID;

        public LeaveCommand(String authToken, int gameID) {
            super(authToken);
            this.commandType = CommandType.LEAVE;
            this.gameID = gameID;
        }

        public int getGameID() {
            return gameID;
        }
    }

    public static class ResignCommand extends UserGameCommand {
        private final int gameID;

        public ResignCommand(String authToken, int gameID) {
            super(authToken);
            this.commandType = CommandType.RESIGN;
            this.gameID = gameID;
        }

        public int getGameID() {
            return gameID;
        }
    }
}
