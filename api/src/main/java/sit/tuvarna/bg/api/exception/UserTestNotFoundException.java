package sit.tuvarna.bg.api.exception;

public class UserTestNotFoundException extends RuntimeException {
    private static final String MESSAGE = "UserTest not found.";

    public UserTestNotFoundException() {
        super(MESSAGE);
    }
}