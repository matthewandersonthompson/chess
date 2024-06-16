package websocket.commands;

public class JoinObserver extends UserGameCommand {
    private final int gameID;

    public JoinObserver(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.CONNECT;
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
