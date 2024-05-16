package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        this.color = color;
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
        return color;
    }

    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        return String.format("%s %s", color, type);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case KING -> addMovesInDirections(board, position, moves, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}}, 1);
            case QUEEN -> addMovesInDirections(board, position, moves, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}}, 8);
            case ROOK -> addMovesInDirections(board, position, moves, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}, 8);
            case BISHOP -> addMovesInDirections(board, position, moves, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}, 8);
            case KNIGHT -> addKnightMoves(board, position, moves);
            case PAWN -> addPawnMoves(board, position, moves);
        }
        return moves;
    }

    private void addMovesInDirections(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int[][] directions, int maxDistance) {
        for (int[] direction : directions) {
            ChessPosition current = new ChessPosition(position.getRow() + direction[0], position.getColumn() + direction[1]);
            int distance = 1;
            while (distance <= maxDistance && isWithinBounds(current)) {
                ChessPiece target = board.getPiece(current);
                if (target == null) {
                    moves.add(new ChessMove(position, current, null));
                } else if (target.getTeamColor() != color) {
                    moves.add(new ChessMove(position, current, null));
                    break;
                } else {
                    break; // Stop if we encounter a piece of the same color
                }
                current = new ChessPosition(current.getRow() + direction[0], current.getColumn() + direction[1]);
                distance++;
            }
        }
    }

    private void addKnightMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int[][] knightMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        for (int[] move : knightMoves) {
            ChessPosition destination = new ChessPosition(position.getRow() + move[0], position.getColumn() + move[1]);
            if (isWithinBounds(destination)) {
                addMoveIfValid(board, position, destination, moves);
            }
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        ChessPosition oneStep = new ChessPosition(position.getRow() + direction, position.getColumn());
        ChessPosition twoSteps = new ChessPosition(position.getRow() + 2 * direction, position.getColumn());
        boolean startingRow = (color == ChessGame.TeamColor.WHITE && position.getRow() == 2) || (color == ChessGame.TeamColor.BLACK && position.getRow() == 7);

        if (board.getPiece(oneStep) == null) {
            addPromotionMove(position, oneStep, moves);
            if (startingRow && board.getPiece(twoSteps) == null) {
                moves.add(new ChessMove(position, twoSteps, null));
            }
        }

        ChessPosition diagonalLeft = new ChessPosition(position.getRow() + direction, position.getColumn() - 1);
        ChessPosition diagonalRight = new ChessPosition(position.getRow() + direction, position.getColumn() + 1);
        addMoveIfEnemy(board, position, diagonalLeft, moves);
        addMoveIfEnemy(board, position, diagonalRight, moves);
    }

    private void addPromotionMove(ChessPosition from, ChessPosition to, Collection<ChessMove> moves) {
        boolean promotionRow = (color == ChessGame.TeamColor.WHITE && to.getRow() == 8) || (color == ChessGame.TeamColor.BLACK && to.getRow() == 1);
        if (promotionRow) {
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addMoveIfValid(ChessBoard board, ChessPosition from, ChessPosition to, Collection<ChessMove> moves) {
        ChessPiece target = board.getPiece(to);
        if (target == null || target.getTeamColor() != color) {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addMoveIfEnemy(ChessBoard board, ChessPosition from, ChessPosition to, Collection<ChessMove> moves) {
        ChessPiece target = board.getPiece(to);
        if (target != null && target.getTeamColor() != color) {
            addPromotionMove(from, to, moves);
        }
    }

    private boolean isWithinBounds(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
    }
}
