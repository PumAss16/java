package coursehub;

/** Неверный аргумент или нарушенное состояние: ошибка программиста (unchecked). */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
