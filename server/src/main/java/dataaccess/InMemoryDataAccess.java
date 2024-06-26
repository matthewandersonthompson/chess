package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryDataAccess implements DataAccessInterface, UserDAO, GameDAO, AuthDAO {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private final AtomicInteger gameIDGenerator = new AtomicInteger(1);

    @Override
    public void clear() {
        users.clear();
        games.clear();
        authTokens.clear();
        gameIDGenerator.set(1);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = users.get(username);
        if (user == null) {
            throw new DataAccessException("User not found");
        }
        return user;
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        return getUser(username);
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        for (GameData existingGame : games.values()) {
            if (existingGame.getGameName().equals(game.getGameName())) {
                throw new DataAccessException("Game with this name already exists");
            }
        }

        int gameID = gameIDGenerator.getAndIncrement();
        game.setGameID(gameID);
        games.put(gameID, game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public GameData getGameById(int gameID) throws DataAccessException {
        return getGame(gameID);
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.getGameID())) {
            throw new DataAccessException("Game not found");
        }
        games.put(game.getGameID(), game);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authTokens.containsKey(auth.getAuthToken())) {
            throw new DataAccessException("Auth token already exists");
        }
        authTokens.put(auth.getAuthToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = authTokens.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Auth token not found");
        }
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authTokens.remove(authToken) == null) {
            throw new DataAccessException("Auth token not found");
        }
    }

    @Override
    public void deleteAuthByUsername(String username) throws DataAccessException {
        authTokens.entrySet().removeIf(entry -> entry.getValue().getUsername().equals(username));
    }


    @Override
    public int getLatestGameID() {
        return gameIDGenerator.get() - 1;
    }
}
