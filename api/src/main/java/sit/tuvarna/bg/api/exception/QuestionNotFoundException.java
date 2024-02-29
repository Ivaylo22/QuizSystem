package sit.tuvarna.bg.api.exception;

public class QuestionNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Question not found.";

    public QuestionNotFoundException() {
        super(MESSAGE);
    }
}
