package requests;

import websocket.commands.UserGameCommand;

public class ResignGameRequest extends UserGameCommand.ResignCommand {
    public ResignGameRequest(String authToken, int gameID) {
        super(authToken, gameID);
    }

    @Override
    public int getGameID() {
        return super.getGameID();
    }
}
