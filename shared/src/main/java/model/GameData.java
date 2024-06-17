package model;

public class GameData {
    private int gameID;
    private String gameName;
    private String whiteUsername;
    private String blackUsername;
    private String gameState;

    public GameData(int gameID, String gameName, String whiteUsername, String blackUsername, String gameState) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameState = gameState;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    // Add methods to get and set the board state
    public String getBoardState() {
        return gameState; // Assuming gameState holds the board state
    }

    public void setBoardState(String boardState) {
        this.gameState = boardState; // Assuming gameState holds the board state
    }

    //most people have an object labeled chessgame that includes the entire chessgame object as a json object INCLUDING whos turn it is, I should probably add this
}
