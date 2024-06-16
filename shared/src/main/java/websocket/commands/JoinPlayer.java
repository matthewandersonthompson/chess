package websocket.commands;

public class JoinPlayer extends UserGameCommand {
    private final int gameID;

    public JoinPlayer(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.CONNECT;
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
