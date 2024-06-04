package dataaccess;

import model.Game;

public interface GameDAO {
    void createGame(Game game) throws DataAccessException;
    Game getGameById(int gameId) throws DataAccessException;
    void clear() throws DataAccessException;
}
