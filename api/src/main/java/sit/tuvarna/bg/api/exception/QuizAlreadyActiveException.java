package sit.tuvarna.bg.api.exception;

public class QuizAlreadyActiveException extends RuntimeException {
    private static final String MESSAGE = "Quiz is already active";

    public QuizAlreadyActiveException() {
        super(MESSAGE);
    }
}