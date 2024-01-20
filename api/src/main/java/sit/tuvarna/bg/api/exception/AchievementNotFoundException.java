package sit.tuvarna.bg.api.exception;

public class AchievementNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Achievement not found.";

    public AchievementNotFoundException() {
        super(MESSAGE);
    }
}
