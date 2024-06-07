package service;

import dataaccess.DataAccessInterface;
import dataaccess.DataAccessException;
import model.AuthData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
    private final DataAccessInterface dataAccess;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String validateAuthToken(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            logger.error("Auth token not found: {}", authToken);
            throw new DataAccessException("Auth token not found");
        }
        logger.info("Auth token validated: {}", authToken);
        return authData.getUsername();
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        dataAccess.createAuth(auth);
        logger.info("Auth token created: {}", auth.getAuthToken());
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        logger.info("Attempting to delete auth token: {}", authToken);
        dataAccess.deleteAuth(authToken);
        logger.info("Auth token deleted: {}", authToken);
    }

    public void deleteAuthByUsername(String username) throws DataAccessException {
        logger.info("Deleting all auth tokens for user: {}", username);
        dataAccess.deleteAuthByUsername(username);
        logger.info("Deleted auth tokens for user: {}", username);
    }
}
