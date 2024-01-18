package sit.tuvarna.bg.api.exception;

public class PasswordsDoNotMatchException extends RuntimeException {
    private static final String MESSAGE = "Passwords don't match.";

    public PasswordsDoNotMatchException() {
        super(MESSAGE);
    }
}