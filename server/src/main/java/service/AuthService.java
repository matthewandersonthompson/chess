package service;

import dataaccess.DataAccessInterface;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private final DataAccessInterface dataAccess;

    public AuthService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String validateAuthToken(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        return authData.username();
    }
}
