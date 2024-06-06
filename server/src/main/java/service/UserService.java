package service;

import dataaccess.DataAccessInterface;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final DataAccessInterface dataAccess;

    public UserService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        try {
            dataAccess.getUser(user.getUsername());
            throw new DataAccessException("Username already taken");
        } catch (DataAccessException e) {
            if (e.getMessage().equals("User not found")) {
                // Hash the password before storing it
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);
                dataAccess.createUser(user);
                String authToken = UUID.randomUUID().toString();
                AuthData auth = new AuthData(authToken, user.getUsername());
                dataAccess.createAuth(auth);
                return auth;
            } else {
                throw e;
            }
        }
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new DataAccessException("Invalid username or password");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        dataAccess.createAuth(auth);
        return auth;
    }

    public void logout(String authToken) throws DataAccessException {
        dataAccess.deleteAuth(authToken);
    }
}
