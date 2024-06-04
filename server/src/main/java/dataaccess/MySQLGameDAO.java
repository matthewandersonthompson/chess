package dataaccess;

import model.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLGameDAO implements GameDAO {

    @Override
    public void createGame(Game game) throws DataAccessException {
        String sql = "INSERT INTO games (game_name, white_player, black_player, game_state) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getGameName());
            stmt.setInt(2, game.getWhitePlayer());
            stmt.setInt(3, game.getBlackPlayer());
            stmt.setString(4, game.getGameState());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting game into the database", e);
        }
    }

    @Override
    public Game getGameById(int gameId) throws DataAccessException {
        Game game = null;
        String sql = "SELECT * FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                game = new Game(
                        rs.getInt("id"),
                        rs.getString("game_name"),
                        rs.getInt("white_player"),
                        rs.getInt("black_player"),
                        rs.getString("game_state")
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding game in the database", e);
        }
        return game;
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing games from the database", e);
        }
    }
}
