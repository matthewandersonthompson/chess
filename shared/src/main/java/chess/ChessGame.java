package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;
    private Map<ChessMove, ChessPiece> capturedPieces;
    private boolean gameOver;


    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTurn = TeamColor.WHITE;
        this.capturedPieces = new HashMap<>();
        this.gameOver = false;
    }


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


    private boolean leavesKingInCheck(TeamColor teamColor, ChessMove move) {
        executeMove(move);
        boolean inCheck = isInCheck(teamColor);
        undoMove(move);
        return inCheck;
    }

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


    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        return kingPosition != null && isPositionUnderAttack(kingPosition, teamColor);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return !hasAnyValidMove(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return !hasAnyValidMove(teamColor);
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public TeamColor getTeamTurn() {
        return this.currentTurn;
    }

    public void setTeamTurn(TeamColor teamTurn) {
        this.currentTurn = teamTurn;
    }

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

    private void undoMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(end);
        board.setPiece(start, piece);
        board.setPiece(end, capturedPieces.remove(move));
    }

    private void toggleTurn() {
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public enum TeamColor {
        WHITE, BLACK;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
