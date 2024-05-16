package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;

    public ChessGame() {
        this.board = new ChessBoard(); // Assuming ChessBoard is properly initialized elsewhere
        this.board.resetBoard(); // Setup the board with initial piece placement
        this.currentTurn = TeamColor.WHITE; // White starts the game
    }

    public Collection<ChessMove> validMoves(ChessPosition position) {
        if (board.isPositionValid(position)) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() == currentTurn) {
                return piece.pieceMoves(board, position);
            }
        }
        return new ArrayList<>(); // Return an empty collection if no valid moves
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null || piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("No piece at the start position or not your turn.");
        }

        // Check if the move is legal by confirming it's in the collection of valid moves
        Collection<ChessMove> validMoves = piece.pieceMoves(board, start);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move for the piece at the given position.");
        }

        // Check if the move passes through other pieces (excluding knight moves)
        if (piece.getPieceType() != ChessPiece.PieceType.KNIGHT && passesThroughOtherPieces(start, end)) {
            throw new InvalidMoveException("Move cannot pass through other pieces.");
        }

        executeMove(move);
        if (isInCheck(currentTurn)) {
            undoMove(move);
            throw new InvalidMoveException("Move puts or leaves king in check.");
        }

        toggleTurn();
    }

    private boolean passesThroughOtherPieces(ChessPosition start, ChessPosition end) {
        int rowStep = Integer.compare(end.getRow(), start.getRow());
        int colStep = Integer.compare(end.getColumn(), start.getColumn());

        int currentRow = start.getRow() + rowStep;
        int currentCol = start.getColumn() + colStep;

        while (currentRow != end.getRow() || currentCol != end.getColumn()) {
            ChessPosition currentPosition = new ChessPosition(currentRow, currentCol);
            if (board.getPiece(currentPosition) != null) {
                return true; // There is a piece in the way
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return false; // No pieces in the way
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        return kingPosition != null && isPositionUnderAttack(kingPosition, teamColor);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false; // Not in check, so cannot be in checkmate.
        }

        return !hasAnyValidMove(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false; // If in check, it's not a stalemate.
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
        // Check if the move includes a promotion
        if (move.getPromotion() != null && (end.getRow() == 1 || end.getRow() == 8)) {
            // If there's a promotion, create a new piece of the promoted type at the destination
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
        board.removePiece(end);
    }

    private void toggleTurn() {
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public enum TeamColor {
        WHITE, BLACK;
    }
}
