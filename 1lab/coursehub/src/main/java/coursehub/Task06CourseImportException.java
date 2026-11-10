package coursehub;

public class Task06CourseImportException extends Exception {

    private final int recordNumber;

    public Task06CourseImportException(int recordNumber, String message, Throwable cause) {
        super(message, cause);
        this.recordNumber = recordNumber;
    }

    public int recordNumber() {
        return recordNumber;
    }

    public int processed() {
        return recordNumber - 1;
    }

    public String code() {
        return "CH-3001";
    }
}
