package service;

import dataaccess.DataAccessInterface;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import java.util.UUID;

public class UserService {
    private final DataAccessInterface dataAccess;

    public UserService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        try {
            dataAccess.getUser(user.username());
            throw new DataAccessException("Username already taken");
        } catch (DataAccessException e) {
            if (e.getMessage().equals("User not found")) {
                dataAccess.createUser(user);
                String authToken = UUID.randomUUID().toString();
                AuthData auth = new AuthData(authToken, user.username());
                dataAccess.createAuth(auth);
                return auth;
            } else {
                throw e;
            }
        }
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);
        if (user.password().equals(password)) {
            String authToken = UUID.randomUUID().toString();
            AuthData auth = new AuthData(authToken, username);
            dataAccess.createAuth(auth);
            return auth;
        } else {
            throw new DataAccessException("Invalid username or password");
        }
    }

    public void logout(String authToken) throws DataAccessException {
        dataAccess.deleteAuth(authToken);
    }
}