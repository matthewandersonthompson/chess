package chess;

public class ChessMove {

    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPieceType; // Add this field

    // Constructor without promotion
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPieceType = null; // Default to null if not a promotion move
    }

    // Constructor with promotion
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPieceType) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPieceType = promotionPieceType; // Set the promotion piece type
    }

    public ChessPosition getStartPosition() {
        return startPosition;
    }

    public ChessPosition getEndPosition() {
        return endPosition;
    }

    public ChessPiece.PieceType getPromotionPieceType() {
        return promotionPieceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessMove chessMove = (ChessMove) o;

        if (!startPosition.equals(chessMove.startPosition)) return false;
        if (!endPosition.equals(chessMove.endPosition)) return false;
        return promotionPieceType == chessMove.promotionPieceType;
    }

    @Override
    public int hashCode() {
        int result = startPosition.hashCode();
        result = 31 * result + endPosition.hashCode();
        result = 31 * result + (promotionPieceType != null ? promotionPieceType.hashCode() : 0);
        return result;
    }
}
