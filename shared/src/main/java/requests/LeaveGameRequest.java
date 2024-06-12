package requests;

public class LeaveGameRequest {
    private final int gameID;

    public LeaveGameRequest(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
