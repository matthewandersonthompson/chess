package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != teamTurn) {
            return null;
        }

        Set<ChessMove> validMoves = new HashSet<>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            ChessBoard tempBoard = copyBoard(board);
            try {
                tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(move.getStartPosition()));
                tempBoard.addPiece(move.getStartPosition(), null);
                if (!isInCheck(teamTurn, tempBoard)) {
                    validMoves.add(move);
                }
            } catch (Exception ignored) {
            }
        }
        return validMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != teamTurn || !validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }

        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);

        if (isInCheck(teamTurn)) {
            throw new InvalidMoveException();
        }

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }

    private boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPosition = findKingPosition(teamColor, board);
        return isPositionAttacked(kingPosition, teamColor, board);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        for (ChessPosition position : getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() == teamColor) {
                for (ChessMove move : validMoves(position)) {
                    try {
                        ChessBoard tempBoard = copyBoard(board);
                        tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(move.getStartPosition()));
                        tempBoard.addPiece(move.getStartPosition(), null);
                        if (!isInCheck(teamColor, tempBoard)) {
                            return false;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return true;
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (ChessPosition position : getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() == teamColor) {
                for (ChessMove move : validMoves(position)) {
                    try {
                        ChessBoard tempBoard = copyBoard(board);
                        tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(move.getStartPosition()));
                        tempBoard.addPiece(move.getStartPosition(), null);
                        if (!isInCheck(teamColor, tempBoard)) {
                            return false;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return true;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKingPosition(TeamColor teamColor, ChessBoard board) {
        for (ChessPosition position : getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                return position;
            }
        }
        return null;
    }

    private boolean isPositionAttacked(ChessPosition position, TeamColor teamColor, ChessBoard board) {
        for (ChessPosition otherPosition : getAllPositions()) {
            ChessPiece piece = board.getPiece(otherPosition);
            if (piece != null && piece.getTeamColor() != teamColor) {
                for (ChessMove move : piece.pieceMoves(board, otherPosition)) {
                    if (move.getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ChessBoard copyBoard(ChessBoard original) {
        ChessBoard copy = new ChessBoard();
        for (ChessPosition position : getAllPositions()) {
            ChessPiece piece = original.getPiece(position);
            if (piece != null) {
                copy.addPiece(position, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
            }
        }
        return copy;
    }

    private Set<ChessPosition> getAllPositions() {
        Set<ChessPosition> positions = new HashSet<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                positions.add(new ChessPosition(row, col));
            }
        }
        return positions;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }
}
