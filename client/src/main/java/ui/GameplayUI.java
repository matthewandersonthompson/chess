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
        // Future phases will add gameplay functionality here
    }

    private void drawBoard(ChessBoard board, boolean isWhiteBottom) {
        System.out.println("Drawing board with " + (isWhiteBottom ? "white" : "black") + " at bottom:");

        for (int row = isWhiteBottom ? 8 : 1; isWhiteBottom ? row >= 1 : row <= 8; row += isWhiteBottom ? -1 : 1) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    System.out.print(piece);
                } else {
                    System.out.print((row + col) % 2 == 0 ? EscapeSequences.EMPTY : " ");
                }
            }
            System.out.println();
        }
    }
}
