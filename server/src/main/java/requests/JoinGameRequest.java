package requests;

public class JoinGameRequest {
    private String playerColor;
    private int gameId;

    public JoinGameRequest(String playerColor, int gameId) {
        this.playerColor = playerColor;
        this.gameId = gameId;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public int getGameId() {
        return gameId;
    }
}
