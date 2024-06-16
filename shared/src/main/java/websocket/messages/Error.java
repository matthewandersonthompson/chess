package websocket.messages;

public class Error extends ServerMessage {
    private final String error;

    public Error(String error) {
        super(ServerMessageType.ERROR);
        this.error = error;
        this.setErrorMessage(error); // Ensure the errorMessage field is set
    }

    public String getError() {
        return error;
    }

    @Override
    public String getErrorMessage() {
        return error;
    }
}
