package coursehub;

public record Task07Result<T>(T value, String errorCode) {

    public static <T> Task07Result<T> ok(T value) {
        return new Task07Result<>(value, null);
    }

    public static <T> Task07Result<T> fail(String errorCode) {
        if (errorCode == null || !errorCode.startsWith("CH-2")) {
            throw new Task02ValidationException("Result accepts only expected codes, got " + errorCode);
        }
        return new Task07Result<>(null, errorCode);
    }

    public boolean isOk() {
        return errorCode == null;
    }
}
