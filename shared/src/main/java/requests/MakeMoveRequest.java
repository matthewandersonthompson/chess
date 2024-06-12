package requests;

import chess.ChessMove;

public class MakeMoveRequest {
    private int gameID;
    private ChessMove move;

    public MakeMoveRequest(int gameID, ChessMove move) {
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
