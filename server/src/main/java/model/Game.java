package model;

public class Game {
    private int id;
    private String gameName;
    private int whitePlayer;
    private int blackPlayer;
    private String gameState;

    public Game(int id, String gameName, int whitePlayer, int blackPlayer, String gameState) {
        this.id = id;
        this.gameName = gameName;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameState = gameState;
    }

    public int getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public int getWhitePlayer() {
        return whitePlayer;
    }

    public int getBlackPlayer() {
        return blackPlayer;
    }

    public String getGameState() {
        return gameState;
    }
}
