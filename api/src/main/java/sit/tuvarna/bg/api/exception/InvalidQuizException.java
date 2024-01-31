package sit.tuvarna.bg.api.exception;

public class InvalidQuizException extends RuntimeException {
    private static final String MESSAGE = "Invalid quiz.";

    public InvalidQuizException() {
        super(MESSAGE);
    }
}