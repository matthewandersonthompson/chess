package requests;

public class ResignGameRequest {
    private final int gameID;

    public ResignGameRequest(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
