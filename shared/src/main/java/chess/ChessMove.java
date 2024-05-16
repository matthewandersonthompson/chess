package chess;

import java.util.Objects;
import chess.ChessPiece.PieceType;



public class ChessMove {
    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType promotion;

    public ChessMove(ChessPosition start, ChessPosition end, ChessPiece.PieceType promotion) {
        this.start = start;
        this.end = end;
        this.promotion = promotion;
    }

    public ChessPosition getStartPosition() {
        return start;
    }

    public ChessPosition getEndPosition() {
        return end;
    }

    public PieceType getPromotion() {
        return promotion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(start, chessMove.start) && Objects.equals(end, chessMove.end) && promotion == chessMove.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotion);
    }

    @Override
    public String toString() {
        return String.format("move from %s to %s%s", start, end, promotion != null ? String.format("promoted to %s", promotion): "");
    }

}
