package sit.tuvarna.bg.api.exception;

public class DatabaseException extends RuntimeException {
    private static final String MESSAGE = "Database error. Try again later!";

    public DatabaseException() {
        super(MESSAGE);
    }
}