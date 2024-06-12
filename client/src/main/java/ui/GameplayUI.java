package ui;

import chess.*;
import client.ServerFacade;
import websocket.messages.ServerMessage;

import java.util.Collection;
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
        displayHelp();
        while (true) {
            System.out.println("Enter a command:");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "redraw":
                    drawBoard(game.getBoard(), playerColor.equals("WHITE"));
                    break;
                case "leave":
                    handleLeave();
                    return;
                case "move":
                    handleMove();
                    break;
                case "resign":
                    handleResign();
                    break;
                case "highlight":
                    handleHighlight();
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
        System.out.println("  resign    - Resign from the game");
        System.out.println("  highlight - Highlight legal moves");
    }

    private void handleLeave() {
        System.out.println("Leaving the game...");
        try {
            serverFacade.leaveGame(gameID);
            System.out.println("You have left the game.");
            notify("You have left the game.");
        } catch (Exception e) {
            System.out.println("Error leaving the game: " + e.getMessage());
        }
    }

    private void handleMove() {
        System.out.println("Enter your move (e.g., e2 e4):");
        String moveInput = scanner.nextLine().trim();
        String[] positions = moveInput.split(" ");
        if (positions.length != 2) {
            System.out.println("Invalid input. Please enter the move in the format 'e2 e4'.");
            return;
        }

        ChessPosition startPosition = parsePosition(positions[0]);
        ChessPosition endPosition = parsePosition(positions[1]);
        ChessMove move = new ChessMove(startPosition, endPosition, null);

        try {
            serverFacade.makeMove(gameID, move);
            System.out.println("Move made successfully.");
            notify("Move made from " + move.getStartPosition() + " to " + move.getEndPosition());
        } catch (Exception e) {
            System.out.println("Error making move: " + e.getMessage());
        }
    }

    private void handleResign() {
        System.out.println("Are you sure you want to resign? (yes/no)");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes")) {
            System.out.println("You have resigned from the game.");
            try {
                serverFacade.resignGame(gameID);
                notify("You have resigned from the game.");
            } catch (Exception e) {
                System.out.println("Error resigning from the game: " + e.getMessage());
            }
        } else {
            System.out.println("Resignation cancelled.");
        }
    }

    private void handleHighlight() {
        System.out.println("Enter the position of the piece to highlight (e.g., e2):");
        String positionInput = scanner.nextLine().trim();
        ChessPosition position = parsePosition(positionInput);

        try {
            Collection<ChessMove> legalMoves = game.validMoves(position);
            highlightMoves(position, legalMoves);
        } catch (Exception e) {
            System.out.println("Error highlighting moves: " + e.getMessage());
        }
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

    private ChessPosition parsePosition(String pos) {
        int col = pos.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(pos.charAt(1));
        return new ChessPosition(row, col);
    }

    private void highlightMoves(ChessPosition position, Collection<ChessMove> legalMoves) {
        System.out.println("Highlighting moves for position: " + position);
        for (ChessMove move : legalMoves) {
            System.out.println("Legal move to: " + move.getEndPosition());
        }
        drawBoard(game.getBoard(), playerColor.equals("WHITE"));
    }

    private void notify(String message) {
        System.out.println("Notification: " + message);
    }
}
