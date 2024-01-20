package sit.tuvarna.bg.api.exception;

public class AchievementAlreadyEarnedException  extends RuntimeException {
    private static final String MESSAGE = "Achievement have already been earned.";

    public AchievementAlreadyEarnedException() {
        super(MESSAGE);
    }
}
