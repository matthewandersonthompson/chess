package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;
    private Map<ChessMove, ChessPiece> capturedPieces;
    private boolean gameOver; // Add this field

    //initialize all of the stuff for ChessGame
    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTurn = TeamColor.WHITE;
        this.capturedPieces = new HashMap<>();
        this.gameOver = false; // Initialize the field
    }

    //return all of the valid moves for a piece at a given position
    public Collection<ChessMove> validMoves(ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (board.isPositionValid(position)) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null) {
                moves = piece.pieceMoves(board, position);
                moves.removeIf(move -> leavesKingInCheck(piece.getTeamColor(), move));
            }
        }
        return moves;
    }

    //check if any move that happens might leave the king in check
    private boolean leavesKingInCheck(TeamColor teamColor, ChessMove move) {
        executeMove(move);
        boolean inCheck = isInCheck(teamColor);
        undoMove(move);
        return inCheck;
    }

    //if a move is valid, execute it
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null || piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("No piece at the start position or not your turn.");
        }

        Collection<ChessMove> validMoves = validMoves(start);
        if (!validMoves.contains(move) || leavesKingInCheck(currentTurn, move)) {
            throw new InvalidMoveException("Invalid move for the piece at the given position.");
        }

        executeMove(move);
        if (isInCheck(currentTurn)) {
            undoMove(move);
            throw new InvalidMoveException("Move puts or leaves king in check.");
        }

        toggleTurn();
    }

    //now check if the specific king is in check
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        return kingPosition != null && isPositionUnderAttack(kingPosition, teamColor);
    }

    //and check if the specific TEAM is in checkMATE
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return !hasAnyValidMove(teamColor);
    }

    //is the team in stalemate
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return !hasAnyValidMove(teamColor);
    }

    //returns the board
    public ChessBoard getBoard() {
        return this.board;
    }

    //set the chess board to another board provided
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    //get the team color for whoever's turn it is
    public TeamColor getTeamTurn() {
        return this.currentTurn;
    }

    //sets team color
    public void setTeamTurn(TeamColor teamTurn) {
        this.currentTurn = teamTurn;
    }

    //get the position of king
    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    //ARE WE UNDER ATTACK?
    private boolean isPositionUnderAttack(ChessPosition position, TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //do we have a valid move?
    private boolean hasAnyValidMove(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        executeMove(move);
                        boolean stillInCheck = isInCheck(teamColor);
                        undoMove(move);
                        if (!stillInCheck) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //execute the move
    private void executeMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        capturedPieces.put(move, board.getPiece(end));
        if (move.getPromotion() != null && (end.getRow() == 1 || end.getRow() == 8)) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotion());
        }
        board.setPiece(end, piece);
        board.removePiece(start);
    }

    //undo the move
    private void undoMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(end);
        board.setPiece(start, piece);
        board.setPiece(end, capturedPieces.remove(move));
    }

    //toggle between white and black if necessary
    private void toggleTurn() {
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public enum TeamColor {
        WHITE, BLACK;
    }

    // Add the new methods
    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
