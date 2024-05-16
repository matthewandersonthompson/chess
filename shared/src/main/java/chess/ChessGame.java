package chess;

import java.util.Collection;

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
        if (piece == null) {
            return null;
        }
        return piece.pieceMoves(board, startPosition);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Invalid move: no piece at start position or not player's turn");
        }

        Collection<ChessMove> validMoves = validMoves(start);
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move: move not in valid moves list");
        }

        System.out.println("Making move: " + move);
        board.makeMove(move);

        if (isInCheck(teamTurn)) {
            board.undoMove(move);
            throw new InvalidMoveException("Invalid move: leaves player in check");
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (end.getRow() == 1 || end.getRow() == 8)) {
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPieceType());
            board.addPiece(end, promotedPiece);
        }

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                    for (ChessMove move : moves) {
                        try {
                            ChessBoard tempBoard = copyBoard(board);
                            tempBoard.makeMove(move);
                            ChessGame tempGame = new ChessGame();
                            tempGame.setBoard(tempBoard);
                            if (!tempGame.isInCheck(teamColor)) {
                                return false;
                            }
                        } catch (InvalidMoveException e) {
                            // Continue checking other moves
                        }
                    }
                }
            }
        }

        return true;
    }

    private ChessBoard copyBoard(ChessBoard original) {
        ChessBoard copy = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = original.getPiece(pos);
                if (piece != null) {
                    copy.addPiece(pos, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return copy;
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                    for (ChessMove move : moves) {
                        try {
                            ChessBoard tempBoard = copyBoard(board);
                            tempBoard.makeMove(move);
                            ChessGame tempGame = new ChessGame();
                            tempGame.setBoard(tempBoard);
                            if (!tempGame.isInCheck(teamColor)) {
                                return false;
                            }
                        } catch (InvalidMoveException e) {
                            // Continue checking other moves
                        }
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

    public enum TeamColor {
        WHITE,
        BLACK
    }
}
