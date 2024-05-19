package sit.tuvarna.bg.api.exception;

public class TestNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Test not found.";

    public TestNotFoundException() {
        super(MESSAGE);
    }
}