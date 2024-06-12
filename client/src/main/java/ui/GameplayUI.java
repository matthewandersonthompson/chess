package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GameplayUI {
    private final ChessGame game;
    private final String playerColor;
    private final int gameID;
    private final ServerFacade serverFacade;
    private final Scanner scanner = new Scanner(System.in);

    public GameplayUI(ChessGame game, String playerColor, int gameID, ServerFacade serverFacade) {
        this.game = game;
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.serverFacade = serverFacade;
    }

    public void display() {
        System.out.println("Gameplay started! Your color: " + playerColor);
        drawBoard(game.getBoard(), playerColor.equals("WHITE"));
        while (true) {
            System.out.println("Enter a command (help, redraw, leave, move, resign, highlight):");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "redraw":
                    drawBoard(game.getBoard(), playerColor.equals("WHITE"));
                    break;
                case "leave":
                    leaveGame();
                    return;
                case "move":
                    makeMove();
                    break;
                case "resign":
                    resignGame();
                    break;
                case "highlight":
                    highlightLegalMoves();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
    }

    private void displayHelp() {
        System.out.println("Commands:");
        System.out.println("  help      - Display this help text");
        System.out.println("  redraw    - Redraw the chess board");
        System.out.println("  leave     - Leave the game");
        System.out.println("  move      - Make a move");
        System.out.println("  resign    - Resign the game");
        System.out.println("  highlight - Highlight legal moves");
    }

    private void drawBoard(ChessBoard board, boolean isWhiteBottom) {
        System.out.println("Drawing board with " + (isWhiteBottom ? "white" : "black") + " at bottom:");
        if (isWhiteBottom) {
            drawBoardWithLabels(board, 8, 1, -1, true);
        } else {
            drawBoardWithLabels(board, 1, 8, 1, false);
        }
    }

    private void drawBoardWithLabels(ChessBoard board, int startRow, int endRow, int rowStep, boolean isWhiteBottom) {
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
                boolean isWhiteSquare = isWhiteBottom ? (row + col) % 2 != 0 : (row + col) % 2 == 0;

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

    private void makeMove() {
        System.out.println("Enter your move in the format 'startRow startCol endRow endCol' (e.g., 2 2 3 2 for moving a piece from b2 to b3):");
        try {
            int startRow = Integer.parseInt(scanner.next());
            int startCol = Integer.parseInt(scanner.next());
            int endRow = Integer.parseInt(scanner.next());
            int endCol = Integer.parseInt(scanner.next());
            ChessMove move = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), null);
            serverFacade.makeMove(gameID, move);
            System.out.println("Move made successfully!");
            drawBoard(game.getBoard(), playerColor.equals("WHITE"));
        } catch (Exception e) {
            System.out.println("Error making move: " + e.getMessage());
        }
    }

    private void leaveGame() {
        System.out.println("Leaving the game...");
        // Handle leaving the game (e.g., notify the server, clean up resources)
    }

    private void resignGame() {
        System.out.println("Are you sure you want to resign? (yes/no)");
        String confirmation = scanner.next().trim().toLowerCase();
        if ("yes".equals(confirmation)) {
            System.out.println("You have resigned from the game.");
            // Handle resigning the game (e.g., notify the server)
        } else {
            System.out.println("Resignation cancelled.");
        }
    }

    private void highlightLegalMoves() {
        System.out.println("Enter the position of the piece you want to highlight in the format 'row col' (e.g., 2 2 for the piece at b2):");
        try {
            int row = Integer.parseInt(scanner.next());
            int col = Integer.parseInt(scanner.next());
            ChessPosition position = new ChessPosition(row, col);
            var legalMoves = game.getLegalMoves(position); // Ensure you have a method to get legal moves in ChessGame
            var board = game.getBoard();
            for (ChessMove move : legalMoves) {
                var endPos = move.getEndPosition();
                board.highlightMove(endPos); // Ensure you have a method to highlight a position on the board
            }
            drawBoard(board, playerColor.equals("WHITE"));
        } catch (Exception e) {
            System.out.println("Error highlighting moves: " + e.getMessage());
        }
    }

    private void handleServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION:
                System.out.println("Notification: " + message.getMessage());
                break;
            case ERROR:
                System.out.println("Error: " + message.getErrorMessage());
                break;
            case LOAD_GAME:
                System.out.println("Loading game state...");
                // Update game state and redraw board
                drawBoard(game.getBoard(), playerColor.equals("WHITE"));
                break;
        }
    }
}
