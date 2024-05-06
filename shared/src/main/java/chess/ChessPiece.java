package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    /**
     * Constructor to initialize a chess piece
     *
     * @param pieceColor Team color of the piece (White or Black)
     * @param type       Type of the chess piece (King, Queen, etc.)
     */
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * Enum representing the various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Override the equals method to compare chess pieces
     *
     * @param o Other object to compare against
     * @return True if the two chess pieces are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    /**
     * Generate a unique hash code for this chess piece based on all attributes
     *
     * @return Unique hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Provide a string representation of the ChessPiece object
     *
     * @return String representation of the chess piece
     */
    @Override
    public String toString() {
        return String.format("%s %s", pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
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

    // Add King moves (one square in any direction)
    private void addKingMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        addDirectionalMoves(board, myPosition, moves, directions, 1);
    }

    // Add Queen moves (combination of rook and bishop moves)
    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        addDirectionalMoves(board, myPosition, moves, directions, 8);
    }

    // Add Rook moves (horizontal and vertical lines)
    private void addRookMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        addDirectionalMoves(board, myPosition, moves, directions, 8);
    }

    // Add Bishop moves (diagonal lines)
    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        addDirectionalMoves(board, myPosition, moves, directions, 8);
    }

    // Add Knight moves (L-shaped jumps)
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

    // Add Pawn moves (including capturing and promotion)
    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        ChessPosition forwardOne = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.getPiece(forwardOne) == null) {
            addMoveWithPromotion(board, myPosition, forwardOne, moves);
            // First move can be two squares
            if ((pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2)
                    || (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)) {
                ChessPosition forwardTwo = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
                if (board.getPiece(forwardTwo) == null) {
                    addMoveIfValid(board, myPosition, forwardTwo, moves);
                }
            }
        }

        // Capture diagonally
        ChessPosition captureLeft = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
        ChessPosition captureRight = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
        addMoveIfEnemy(board, myPosition, captureLeft, moves);
        addMoveIfEnemy(board, myPosition, captureRight, moves);
    }

    // Helper method to add a move if valid and not blocked by friendly pieces
    private void addMoveIfValid(ChessBoard board, ChessPosition from, ChessPosition to, List<ChessMove> moves) {
        if (isWithinBounds(to) && (board.getPiece(to) == null || board.getPiece(to).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(from, to, null));
        }
    }

    // Helper method to add a move if the target square is occupied by an enemy piece
    private void addMoveIfEnemy(ChessBoard board, ChessPosition from, ChessPosition to, List<ChessMove> moves) {
        ChessPiece target = board.getPiece(to);
        if (isWithinBounds(to) && target != null && target.getTeamColor() != pieceColor) {
            addMoveWithPromotion(board, from, to, moves);
        }
    }

    // Helper method to add a move considering pawn promotion
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

    // Helper method to add moves in specific directions (linear moves)
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
                    break; // Stop on enemy piece
                } else {
                    break; // Stop on friendly piece
                }
                current = new ChessPosition(current.getRow() + rowOffset, current.getColumn() + colOffset);
                distance++;
            }
        }
    }

    // Check if a given position is within the chessboard bounds
    private boolean isWithinBounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
