package requests;

public class JoinGameRequest {
    private String playerColor;
    private int gameID;

    public JoinGameRequest(String playerColor, int gameId) {
        this.playerColor = playerColor;
        this.gameID = gameId;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public int getGameId() {
        return gameID;
    }
}
