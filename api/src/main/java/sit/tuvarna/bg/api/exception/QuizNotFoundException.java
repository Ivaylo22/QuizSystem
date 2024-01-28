package sit.tuvarna.bg.api.exception;

public class QuizNotFoundException  extends RuntimeException {
    private static final String MESSAGE = "Quiz not found.";

    public QuizNotFoundException() {
        super(MESSAGE);
    }
}
