package service;

import dataaccess.DataAccessInterface;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UserService {
    private final DataAccessInterface dataAccess;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);  // Changed

    public UserService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        try {
            dataAccess.getUser(user.getUsername());
            throw new DataAccessException("Username already taken");
        } catch (DataAccessException e) {
            if (e.getMessage().equals("User not found")) {
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);
                dataAccess.createUser(user);
                String authToken = UUID.randomUUID().toString();
                AuthData auth = new AuthData(authToken, user.getUsername());
                dataAccess.createAuth(auth);
                LOGGER.info("User registered successfully: {}", user.getUsername());
                return auth;
            } else {
                throw e;
            }
        }
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            LOGGER.error("Invalid username or password for user: {}", username);
            throw new DataAccessException("Invalid username or password");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        dataAccess.createAuth(auth);
        LOGGER.info("User logged in successfully: {} with new token: {}", username, authToken);
        return auth;
    }

    public void logout(String authToken) throws DataAccessException {
        LOGGER.info("Attempting to log out with token: {}", authToken);
        dataAccess.deleteAuth(authToken);
        LOGGER.info("User logged out successfully, auth token deleted: {}", authToken);
    }
}
