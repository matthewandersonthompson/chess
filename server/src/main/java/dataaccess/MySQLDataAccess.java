package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDataAccess implements DataAccessInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLDataAccess.class);

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM auth_tokens");
                stmt.executeUpdate("DELETE FROM moves");
                stmt.executeUpdate("DELETE FROM games");
                stmt.executeUpdate("DELETE FROM users");
                LOGGER.info("Database cleared successfully");
            }
        } catch (SQLException e) {
            LOGGER.error("Error clearing tables: {}", e.getMessage());
            throw new DataAccessException("Error clearing tables", e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
            LOGGER.info("User created successfully: {}", user.getUsername());
        } catch (SQLException e) {
            LOGGER.error("Error inserting user: {}", e.getMessage());
            throw new DataAccessException("Error inserting user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                } else {
                    throw new DataAccessException("User not found");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching user: {}", e.getMessage());
            throw new DataAccessException("Error fetching user", e);
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (game_name, white_username, black_username, game_state) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, game.getGameName());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, game.getGameState());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    game.setGameID(generatedKeys.getInt(1));
                    LOGGER.info("Game created successfully: {}", game.getGameID());
                } else {
                    throw new DataAccessException("Creating game failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error inserting game: {}", e.getMessage());
            throw new DataAccessException("Error inserting game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("id"),
                            rs.getString("game_name"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            rs.getString("game_state")
                    );
                } else {
                    throw new DataAccessException("Game not found");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching game: {}", e.getMessage());
            throw new DataAccessException("Error fetching game", e);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("id"),
                        rs.getString("game_name"),
                        rs.getString("white_username"),
                        rs.getString("black_username"),
                        rs.getString("game_state")
                ));
            }
            LOGGER.info("Games listed successfully");
        } catch (SQLException e) {
            LOGGER.error("Error listing games: {}", e.getMessage());
            throw new DataAccessException("Error listing games", e);
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE games SET game_name = ?, white_username = ?, black_username = ?, game_state = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getGameName());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, game.getGameState());
            stmt.setInt(5, game.getGameID());
            stmt.executeUpdate();
            LOGGER.info("Game updated successfully: {}", game.getGameID());
        } catch (SQLException e) {
            LOGGER.error("Error updating game: {}", e.getMessage());
            throw new DataAccessException("Error updating game", e);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.getAuthToken());
            stmt.setString(2, auth.getUsername());
            stmt.executeUpdate();
            LOGGER.info("Auth token created successfully for user: {}", auth.getUsername());
        } catch (SQLException e) {
            LOGGER.error("Error inserting auth token: {}", e.getMessage());
            throw new DataAccessException("Error inserting auth token", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth_tokens WHERE auth_token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("auth_token"),
                            rs.getString("username")
                    );
                } else {
                    throw new DataAccessException("Auth token not found");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching auth token: {}", e.getMessage());
            throw new DataAccessException("Error fetching auth token", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE auth_token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.error("Auth token not found in database: {}", authToken);
                throw new DataAccessException("Auth token not found");
            }
            LOGGER.info("Auth token deleted: {}", authToken);
        } catch (SQLException e) {
            LOGGER.error("Error deleting auth token: {}", authToken, e);
            throw new DataAccessException("Error deleting auth token", e);
        }
    }

    @Override
    public void deleteAuthByUsername(String username) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.error("Auth tokens not found for user: {}", username);
                throw new DataAccessException("Auth tokens not found for user");
            }
            LOGGER.info("Deleted auth tokens for user: {}", username);
        } catch (SQLException e) {
            LOGGER.error("Error deleting auth tokens by username: {}", e.getMessage());
            throw new DataAccessException("Error deleting auth tokens by username", e);
        }
    }

    @Override
    public int getLatestGameID() throws DataAccessException {
        String sql = "SELECT MAX(id) AS max_id FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id");
            } else {
                throw new DataAccessException("Error getting latest game ID");
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting latest game ID: {}", e.getMessage());
            throw new DataAccessException("Error getting latest game ID", e);
        }
    }
}
