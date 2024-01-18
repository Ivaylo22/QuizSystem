package sit.tuvarna.bg.api.exception;

public class UserExistsException extends RuntimeException {
    private static final String MESSAGE = "This user already exists.";

    public UserExistsException() {
        super(MESSAGE);
    }
}
