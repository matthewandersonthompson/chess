package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void deleteAuthByUsername(String username) throws DataAccessException; // New method
}
