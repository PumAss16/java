package coursehub.practice6.time;

class InvalidDateFieldException extends RuntimeException {

    private final String field;
    private final String value;

    InvalidDateFieldException(String field, String value, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
        this.value = value;
    }

    String field() {
        return field;
    }

    String value() {
        return value;
    }
}
