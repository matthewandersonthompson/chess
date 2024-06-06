package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            String createDB = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            stmt.executeUpdate(createDB);

            String useDB = "USE " + DATABASE_NAME;
            stmt.executeUpdate(useDB);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(255) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) UNIQUE NOT NULL" +
                    ")";
            stmt.executeUpdate(createUsersTable);

            String createGamesTable = "CREATE TABLE IF NOT EXISTS games (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "game_name VARCHAR(255) NOT NULL," +
                    "white_player INT," +
                    "black_player INT," +
                    "game_state TEXT," +
                    "FOREIGN KEY (white_player) REFERENCES users(id)," +
                    "FOREIGN KEY (black_player) REFERENCES users(id)" +
                    ")";
            stmt.executeUpdate(createGamesTable);

            String createAuthTokensTable = "CREATE TABLE IF NOT EXISTS auth_tokens (" +
                    "auth_token VARCHAR(255) PRIMARY KEY," +
                    "username VARCHAR(255) NOT NULL," +
                    "FOREIGN KEY (username) REFERENCES users(username)" +
                    ")";
            stmt.executeUpdate(createAuthTokensTable);

            String createMovesTable = "CREATE TABLE IF NOT EXISTS moves (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "game_id INT," +
                    "move VARCHAR(255) NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (game_id) REFERENCES games(id)" +
                    ")";
            stmt.executeUpdate(createMovesTable);

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
