package coursehub;

/** Результат-значение: только ожидаемые коды ошибок (задание 7). */
public record Result<T>(T value, String errorCode) {

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null);
    }

    /** Граница защищена: инфраструктурные и программные ошибки в Result не попадают. */
    public static <T> Result<T> fail(String errorCode) {
        if (errorCode == null || !errorCode.startsWith("CH-2")) {
            throw new ValidationException("Result accepts only expected codes, got " + errorCode);
        }
        return new Result<>(null, errorCode);
    }

    public boolean isOk() {
        return errorCode == null;
    }
}
