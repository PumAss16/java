package coursehub.practice7;

public record ImportError(long lineNumber, String message) {

    public ImportError {
        if (lineNumber < 1) {
            throw new IllegalArgumentException("lineNumber must be >= 1");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }
}
