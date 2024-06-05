package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUserByUsername(String username) throws DataAccessException;
    void clear() throws DataAccessException;
}
