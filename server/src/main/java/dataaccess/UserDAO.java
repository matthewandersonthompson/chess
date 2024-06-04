package dataaccess;

import model.User;

public interface UserDAO {
    void createUser(User user) throws DataAccessException;
    User getUserByUsername(String username) throws DataAccessException;
    void clear() throws DataAccessException;
}
