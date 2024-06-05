package dataaccess;

import model.GameData;

public interface GameDAO {
    void createGame(GameData game) throws DataAccessException;
    GameData getGameById(int gameId) throws DataAccessException;
    void clear() throws DataAccessException;
}
