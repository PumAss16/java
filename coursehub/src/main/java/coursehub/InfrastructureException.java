package coursehub;

/** Сбой инфраструктуры (чтение/закрытие источника): unchecked, cause сохранён. */
public class InfrastructureException extends RuntimeException {

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public String code() {
        return "CH-4001";
    }
}
