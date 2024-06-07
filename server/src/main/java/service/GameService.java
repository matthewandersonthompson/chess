package service;

import dataaccess.DataAccessInterface;
import dataaccess.DataAccessException;
import model.GameData;
import chess.ChessGame;

import java.util.List;

public class GameService {
    private final DataAccessInterface dataAccess;

    public GameService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        GameData gameData = new GameData(0, gameName, 0, 0, new ChessGame().toString());
        dataAccess.createGame(gameData);
        return gameData;
    }

    public void joinGame(int gameID, String username, String playerColor) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }

        if (playerColor == null) {
            throw new DataAccessException("Invalid player color");
        }

        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (gameData.getWhitePlayer() != 0) {
                throw new DataAccessException("White player already taken");
            }
            gameData.setWhitePlayer(username.hashCode()); // using hashCode as a placeholder
            gameData.setWhiteUsername(username);
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (gameData.getBlackPlayer() != 0) {
                throw new DataAccessException("Black player already taken");
            }
            gameData.setBlackPlayer(username.hashCode()); // using hashCode as a placeholder
            gameData.setBlackUsername(username);
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
