package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessInterface;
import model.GameData;
import chess.*;

import java.util.List;

public class GameService {
    private final DataAccessInterface dataAccess;

    public GameService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        GameData gameData = new GameData(0, gameName, null, null, serializeBoard(new ChessGame().getBoard()));
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
        ChessGame game = new ChessGame();
        game.setBoard(deserializeBoard(gameData.getBoardState()));
        return game;
    }

    public void saveGame(int gameID, ChessGame game) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }
        gameData.setGameState(new Gson().toJson(game));
        dataAccess.updateGame(gameData);
    }

    private String serializeBoard(ChessBoard board) {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (piece != null) {
                    builder.append(piece.getTeamColor().name()).append("-").append(piece.getPieceType().name());
                } else {
                    builder.append(" ");
                }
                if (col < 7) {
                    builder.append(",");
                }
            }
            if (row < 7) {
                builder.append("/");
            }
        }
        return builder.toString();
    }

    private ChessBoard deserializeBoard(String boardState) {
        ChessBoard chessBoard = new ChessBoard();
        String[] rows = boardState.split("/");
        for (int row = 0; row < 8; row++) {
            String[] cols = rows[row].split(",");
            for (int col = 0; col < 8; col++) {
                if (!cols[col].equals(" ")) {
                    String[] parts = cols[col].split("-");
                    ChessPiece piece = new ChessPiece(ChessGame.TeamColor.valueOf(parts[0]), ChessPiece.PieceType.valueOf(parts[1]));
                    chessBoard.setPiece(new ChessPosition(row + 1, col + 1), piece);
                }
            }
        }
        return chessBoard;
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

    public String checkForCheckAndCheckmate(ChessGame game, ChessGame.TeamColor teamColor) {
        if (game.isInCheckmate(teamColor)) {
            return "checkmate";
        } else if (game.isInCheck(teamColor)) {
            return "check";
        } else if (game.isInStalemate(teamColor)) {
            return "stalemate";
        }
        return "none";
    }

    // New method to get the player's team color
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

    // New method to get the player's username based on team color
    public String getPlayerUsername(int gameID, ChessGame.TeamColor teamColor) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }
        if (teamColor == ChessGame.TeamColor.WHITE) {
            return gameData.getWhiteUsername();
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            return gameData.getBlackUsername();
        }
        return null;
    }

    // New method to remove player from the game
    public void removePlayer(int gameID, ChessGame.TeamColor teamColor) throws DataAccessException {
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }

        if (teamColor == ChessGame.TeamColor.WHITE) {
            gameData.setWhiteUsername(null);
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            gameData.setBlackUsername(null);
        }

        dataAccess.updateGame(gameData);
    }
}
