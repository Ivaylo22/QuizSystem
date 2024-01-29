package sit.tuvarna.bg.api.exception;

public class QuizAlreadyArchivedException extends RuntimeException {
    private static final String MESSAGE = "Quiz is already archived";

    public QuizAlreadyArchivedException() {
        super(MESSAGE);
    }
}