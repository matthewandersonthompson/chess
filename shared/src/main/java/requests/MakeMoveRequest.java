package requests;

import chess.ChessMove;
import websocket.commands.UserGameCommand;

public class MakeMoveRequest extends UserGameCommand.MakeMoveCommand {
    public MakeMoveRequest(String authToken, int gameID, ChessMove move) {
        super(authToken, gameID, move);
    }

    @Override
    public int getGameID() {
        return super.getGameID();
    }

    public ChessMove getMove() {
        return super.getMove();
    }
}
