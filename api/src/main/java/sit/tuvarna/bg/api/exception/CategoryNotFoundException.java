package sit.tuvarna.bg.api.exception;

public class CategoryNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Category not found.";

    public CategoryNotFoundException() {
        super(MESSAGE);
    }
}
