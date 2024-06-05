package model;

public class GameData {
    private int gameID;
    private String gameName;
    private int whitePlayer;
    private int blackPlayer;
    private String gameState;

    public GameData(int gameID, String gameName, int whitePlayer, int blackPlayer, String gameState) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
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

    public int getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(int whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public int getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(int blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public String getGameState() {
        return gameState;
    }
}