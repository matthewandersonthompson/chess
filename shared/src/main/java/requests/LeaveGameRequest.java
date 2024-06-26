package requests;

import websocket.commands.UserGameCommand;

public class LeaveGameRequest extends UserGameCommand.LeaveCommand {
    public LeaveGameRequest(String authToken, int gameID) {
        super(authToken, gameID);
    }

    @Override
    public int getGameID() {
        return super.getGameID();
    }
}
