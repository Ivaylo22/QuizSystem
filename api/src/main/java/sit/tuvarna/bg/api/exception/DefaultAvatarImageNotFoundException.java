package sit.tuvarna.bg.api.exception;

public class DefaultAvatarImageNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Could not find default avatar image";

    public DefaultAvatarImageNotFoundException() {
        super(MESSAGE);
    }
}