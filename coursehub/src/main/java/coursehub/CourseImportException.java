package coursehub;

/** Ожидаемая ошибка импорта: запись не разобрана (задания 6 и 14). */
public class CourseImportException extends Exception {

    private final int recordNumber;

    public CourseImportException(int recordNumber, String message, Throwable cause) {
        super(message, cause);
        this.recordNumber = recordNumber;
    }

    public int recordNumber() {
        return recordNumber;
    }

    /** Сколько записей обработано до отказа. */
    public int processed() {
        return recordNumber - 1;
    }

    public String code() {
        return "CH-3001";
    }
}
