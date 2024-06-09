package ui;

import client.ServerFacade;
import chess.ChessGame;
import model.GameData;
import results.CreateGameResult;
import results.ListGamesResult;

import java.util.List;
import java.util.Scanner;

public class PostloginUI {
    private final ServerFacade serverFacade;
    private final Scanner scanner = new Scanner(System.in);
    private List<GameData> lastListedGames;

    public PostloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void display() {
        while (true) {
            System.out.println(EscapeSequences.RESET_TEXT_COLOR);
            System.out.println(EscapeSequences.RESET_BG_COLOR);
            System.out.println("Commands:");
            System.out.println("  help          - Display this help text");
            System.out.println("  logout        - Log out of your account");
            System.out.println("  create game   - Create a new game");
            System.out.println("  list games    - List all games");
            System.out.println("  play game     - Join a game to play");
            System.out.println("  observe game  - Join a game to observe (functionality added in Phase 6)");

            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "logout":
                    handleLogout();
                    return; // Transition back to PreloginUI
                case "create game":
                    handleCreateGame();
                    break;
                case "list games":
                    handleListGames();
                    break;
                case "play game":
                    handlePlayGame();
                    break;
                case "observe game":
                    handleObserveGame();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
    }

    private void displayHelp() {
        System.out.println("Help:");
        System.out.println("  help          - Display this help text");
        System.out.println("  logout        - Log out of your account");
        System.out.println("  create game   - Create a new game");
        System.out.println("  list games    - List all games");
        System.out.println("  play game     - Join a game to play");
        System.out.println("  observe game  - Join a game to observe (functionality added in Phase 6)");
    }

    private void handleLogout() {
        System.out.println("Logging out...");
        try {
            serverFacade.logout();
            System.out.println("Logout successful!");
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
        }
    }

    private void handleCreateGame() {
        System.out.println("Enter the name of the new game:");
        String gameName = scanner.nextLine().trim();

        try {
            CreateGameResult result = serverFacade.createGame(gameName);
            System.out.println("Game created successfully! Game ID: " + result.gameID());
        } catch (Exception e) {
            System.out.println("Error during game creation: " + e.getMessage());
        }
    }

    private void handleListGames() {
        try {
            ListGamesResult result = serverFacade.listGames();
            lastListedGames = result.games();
            System.out.println("Games:");
            for (int i = 0; i < lastListedGames.size(); i++) {
                GameData game = lastListedGames.get(i);
                System.out.printf("%d. %s - White: %s, Black: %s%n", i + 1, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
            }
        } catch (Exception e) {
            System.out.println("Error during listing games: " + e.getMessage());
        }
    }

    private void handlePlayGame() {
        if (lastListedGames == null || lastListedGames.isEmpty()) {
            System.out.println("No games listed. Please use the 'list games' command first.");
            return;
        }

        System.out.println("Enter the number of the game you want to join:");
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Please try again.");
            return;
        }

        if (gameNumber < 1 || gameNumber > lastListedGames.size()) {
            System.out.println("Invalid game number. Please try again.");
            return;
        }

        GameData selectedGame = lastListedGames.get(gameNumber - 1);

        System.out.println("Enter the color you want to play (white/black):");
        String color = scanner.nextLine().trim().toUpperCase();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Invalid color. Please try again.");
            return;
        }

        try {
            serverFacade.joinGame(selectedGame.getGameID(), color);
            System.out.println("Joined game successfully!");
            new GameplayUI(new ChessGame(), color).display(); // Transition to GameplayUI
        } catch (Exception e) {
            System.out.println("Error during joining game: " + e.getMessage());
        }
    }

    private void handleObserveGame() {
        // Functionality to be added in Phase 6
        System.out.println("Observe game functionality will be added in Phase 6.");
    }
}
