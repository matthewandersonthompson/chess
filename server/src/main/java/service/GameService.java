package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessInterface;
import model.GameData;
import chess.*;

import java.util.List;

public class GameService {
    private final DataAccessInterface dataAccess;
    private final Gson gson = new Gson();

    public GameService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(0, gameName, null, null, gson.toJson(newGame));
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
            if (gameData.getWhiteUsername() != null) {
                throw new DataAccessException("White player already taken");
            }
            gameData.setWhiteUsername(username);
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (gameData.getBlackUsername() != null) {
                throw new DataAccessException("Black player already taken");
            }
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

    public ChessGame loadGame(int gameID) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }
        return gson.fromJson(gameData.getGameState(), ChessGame.class);
    }

    public void saveGame(int gameID, ChessGame game) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }
        gameData.setGameState(gson.toJson(game));
        dataAccess.updateGame(gameData);
    }

    public boolean isValidGameID(int gameID) throws DataAccessException {
        return dataAccess.getGame(gameID) != null;
    }

    public ChessGame processMove(int gameID, ChessMove move) throws DataAccessException, InvalidMoveException {
        ChessGame game = loadGame(gameID);
        game.makeMove(move);
        saveGame(gameID, game);
        return game;
    }

    public ChessGame.TeamColor getPlayerTeam(int gameID, String username) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }
        if (username.equals(gameData.getWhiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.getBlackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            throw new DataAccessException("User not part of this game");
        }
    }

    public void removePlayer(int gameID, String username) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }

        if (username.equals(gameData.getWhiteUsername())) {
            gameData.setWhiteUsername(null);
        } else if (username.equals(gameData.getBlackUsername())) {
            gameData.setBlackUsername(null);
        } else {
        }

        dataAccess.updateGame(gameData);
    }

}
