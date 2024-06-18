package results;

public class MakeMoveResult {
    private boolean success;
    private String message;

    public MakeMoveResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
