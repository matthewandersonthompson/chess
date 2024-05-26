package service;

import dataaccess.DataAccessInterface;
import dataaccess.DataAccessException;
import model.GameData;
import chess.ChessGame;

import java.util.List;
import java.util.UUID;

public class GameService {
    private final DataAccessInterface dataAccess;

    public GameService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        int gameID = UUID.randomUUID().hashCode();
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        dataAccess.createGame(gameData);
        return gameData;
    }

    public void joinGame(int gameID, String username, String playerColor) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (gameData.whiteUsername() != null) {
                throw new DataAccessException("White player already taken");
            }
            gameData = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (gameData.blackUsername() != null) {
                throw new DataAccessException("Black player already taken");
            }
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        } else {
            throw new DataAccessException("Invalid player color");
        }
        dataAccess.updateGame(gameData);
    }

    public List<GameData> listGames() throws DataAccessException {
        return dataAccess.listGames();
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }
}
