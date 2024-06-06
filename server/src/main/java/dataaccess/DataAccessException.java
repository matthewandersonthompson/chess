package dataaccess;

public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DataAccessException userNotFound() {
        return new DataAccessException("User not found");
    }
}
