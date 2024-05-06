package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return String.format("%s %s", pieceColor, type);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case KING -> addKingMoves(board, myPosition, moves);
            case QUEEN -> addQueenMoves(board, myPosition, moves);
            case ROOK -> addRookMoves(board, myPosition, moves);
            case BISHOP -> addBishopMoves(board, myPosition, moves);
            case KNIGHT -> addKnightMoves(board, myPosition, moves);
            case PAWN -> addPawnMoves(board, myPosition, moves);
        }
        return moves;
    }

    private void addKingMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        addDirectionalMoves(board, myPosition, moves, directions, 1);
    }

    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        addDirectionalMoves(board, myPosition, moves, directions, 8);
    }

    private void addRookMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        addDirectionalMoves(board, myPosition, moves, directions, 8);
    }

    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        addDirectionalMoves(board, myPosition, moves, directions, 8);
    }

    private void addKnightMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move : knightMoves) {
            ChessPosition newPosition = new ChessPosition(
                    myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
            addMoveIfValid(board, myPosition, newPosition, moves);
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        ChessPosition forwardOne = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.getPiece(forwardOne) == null) {
            addMoveWithPromotion(board, myPosition, forwardOne, moves);
            if ((pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2)
                    || (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)) {
                ChessPosition forwardTwo = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
                if (board.getPiece(forwardTwo) == null) {
                    addMoveIfValid(board, myPosition, forwardTwo, moves);
                }
            }
        }

        ChessPosition captureLeft = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
        ChessPosition captureRight = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
        addMoveIfEnemy(board, myPosition, captureLeft, moves);
        addMoveIfEnemy(board, myPosition, captureRight, moves);
    }

    private void addMoveIfValid(ChessBoard board, ChessPosition from, ChessPosition to, List<ChessMove> moves) {
        if (isWithinBounds(to) && (board.getPiece(to) == null || board.getPiece(to).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addMoveIfEnemy(ChessBoard board, ChessPosition from, ChessPosition to, List<ChessMove> moves) {
        ChessPiece target = board.getPiece(to);
        if (isWithinBounds(to) && target != null && target.getTeamColor() != pieceColor) {
            addMoveWithPromotion(board, from, to, moves);
        }
    }

    private void addMoveWithPromotion(ChessBoard board, ChessPosition from, ChessPosition to, List<ChessMove> moves) {
        if ((pieceColor == ChessGame.TeamColor.WHITE && to.getRow() == 8)
                || (pieceColor == ChessGame.TeamColor.BLACK && to.getRow() == 1)) {
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addDirectionalMoves(ChessBoard board, ChessPosition from, List<ChessMove> moves, int[][] directions, int maxDistance) {
        for (int[] direction : directions) {
            int rowOffset = direction[0];
            int colOffset = direction[1];
            ChessPosition current = new ChessPosition(from.getRow() + rowOffset, from.getColumn() + colOffset);
            int distance = 1;
            while (distance <= maxDistance && isWithinBounds(current)) {
                if (board.getPiece(current) == null) {
                    moves.add(new ChessMove(from, current, null));
                } else if (board.getPiece(current).getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(from, current, null));
                    break;
                } else {
                    break;
                }
                current = new ChessPosition(current.getRow() + rowOffset, current.getColumn() + colOffset);
                distance++;
            }
        }
    }

    private boolean isWithinBounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
