package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class GameplayUI {
    private final ChessGame game;
    private final String playerColor;

    public GameplayUI(ChessGame game, String playerColor) {
        this.game = game;
        this.playerColor = playerColor;
    }

    public void display() {
        System.out.println("Gameplay started! Your color: " + playerColor);
        drawBoard(game.getBoard(), playerColor.equals("WHITE"));
        drawBoard(game.getBoard(), !playerColor.equals("WHITE"));
    }

    private void drawBoard(ChessBoard board, boolean isWhiteBottom) {
        System.out.println("Drawing board with " + (isWhiteBottom ? "white" : "black") + " at bottom:");

        if (isWhiteBottom) {
            drawBoardWithLabels(board, 8, 1, -1);
        } else {
            drawBoardWithLabels(board, 1, 8, 1);
        }
    }

    private void drawBoardWithLabels(ChessBoard board, int startRow, int endRow, int rowStep) {
        // Print column labels
        System.out.print(" ");
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(" " + c + " ");
        }
        System.out.println();

        for (int row = startRow; row != endRow + rowStep; row += rowStep) {
            System.out.print(row); // Print row label
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                boolean isWhiteSquare = (row + col) % 2 == 0;

                if (piece != null) {
                    System.out.print(getColoredSquare(isWhiteSquare) + getColoredPiece(piece) + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
                } else {
                    System.out.print(getColoredSquare(isWhiteSquare) + "   " + EscapeSequences.RESET_BG_COLOR);
                }
            }
            System.out.println(" " + row); // Print row label
        }

        // Print column labels again
        System.out.print(" ");
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(" " + c + " ");
        }
        System.out.println();
    }

    private String getColoredSquare(boolean isWhiteSquare) {
        return isWhiteSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
    }

    private String getColoredPiece(ChessPiece piece) {
        String symbol = getPieceSymbol(piece);
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_RED + symbol : EscapeSequences.SET_TEXT_COLOR_BLUE + symbol;
    }

    private String getPieceSymbol(ChessPiece piece) {
        String symbol;
        switch (piece.getPieceType()) {
            case KING:
                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                break;
            case QUEEN:
                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                break;
            case BISHOP:
                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                break;
            case KNIGHT:
                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                break;
            case ROOK:
                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                break;
            case PAWN:
                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                break;
            default:
                symbol = " ";
        }
        return symbol;
    }
}
