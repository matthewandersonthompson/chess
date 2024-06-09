package ui;

import client.ServerFacade;
import results.LoginResult;
import results.RegisterResult;

import java.util.Scanner;

public class PreloginUI {
    private final ServerFacade serverFacade;
    private final Scanner scanner = new Scanner(System.in);

    public PreloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void display() {
        while (true) {
            System.out.println(EscapeSequences.RESET_TEXT_COLOR);
            System.out.println(EscapeSequences.RESET_BG_COLOR);
            System.out.println("Commands:");
            System.out.println("  help    - Display this help text");
            System.out.println("  quit    - Exit the program");
            System.out.println("  login   - Log in to your account");
            System.out.println("  register - Register a new account");

            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "quit":
                    System.out.println("Exiting program...");
                    System.exit(0);
                case "login":
                    handleLogin();
                    break;
                case "register":
                    handleRegister();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
    }

    private void displayHelp() {
        System.out.println("Help:");
        System.out.println("  help    - Display this help text");
        System.out.println("  quit    - Exit the program");
        System.out.println("  login   - Log in to your account");
        System.out.println("  register - Register a new account");
    }

    private void handleLogin() {
        System.out.println("Enter your username:");
        String username = scanner.nextLine().trim();
        System.out.println("Enter your password:");
        String password = scanner.nextLine().trim();

        try {
            LoginResult result = serverFacade.login(username, password);
            System.out.println("Login successful! Auth token: " + result.authToken());
            new PostloginUI(serverFacade).display(); // Transition to PostloginUI
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private void handleRegister() {
        System.out.println("Enter your username:");
        String username = scanner.nextLine().trim();
        System.out.println("Enter your password:");
        String password = scanner.nextLine().trim();
        System.out.println("Enter your email:");
        String email = scanner.nextLine().trim();

        try {
            RegisterResult result = serverFacade.register(username, password, email);
            System.out.println("Registration successful! Auth token: " + result.authToken());
            new PostloginUI(serverFacade).display(); // Transition to PostloginUI
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }
}
