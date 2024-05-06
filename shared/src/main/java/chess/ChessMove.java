package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    /**
     * Constructor to initialize all attributes
     *
     * @param startPosition  Starting square of the move
     * @param endPosition    Ending square of the move
     * @param promotionPiece Type of piece to promote to (if applicable)
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    /**
     * Check if two ChessMove objects are equal based on all attributes
     *
     * @param o Other object to compare against
     * @return True if the two moves are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition)
                && Objects.equals(endPosition, chessMove.endPosition)
                && promotionPiece == chessMove.promotionPiece;
    }

    /**
     * Generate a unique hash code for this move based on all attributes
     *
     * @return Unique hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    /**
     * Check if the move is valid based on the starting and ending positions
     *
     * @return True if the move is valid, false otherwise
     */
    public boolean isValid() {
        return startPosition != null && endPosition != null;
    }

    /**
     * Provide a string representation of the ChessMove object
     *
     * @return String representation of the move
     */
    @Override
    public String toString() {
        return String.format("%s to %s, promote to %s", startPosition, endPosition,
                promotionPiece != null ? promotionPiece : "none");
    }
}
