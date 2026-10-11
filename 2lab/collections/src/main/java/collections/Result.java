package collections;

import java.util.function.Function;

public final class Result<T> {

    private final T value;
    private final String error;

    private Result(T value, String error) {
        if ((value == null) == (error == null)) {
            throw new IllegalStateException("exactly one of value or error is required");
        }
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> ok(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value required");
        }
        return new Result<>(value, null);
    }

    public static <T> Result<T> fail(String error) {
        if (error == null || error.isBlank()) {
            throw new IllegalArgumentException("error required");
        }
        return new Result<>(null, error);
    }

    public boolean isOk() {
        return error == null;
    }

    public T value() {
        if (!isOk()) {
            throw new IllegalStateException("result is a failure: " + error);
        }
        return value;
    }

    public String error() {
        if (isOk()) {
            throw new IllegalStateException("result is a success");
        }
        return error;
    }

    public T orElse(T fallback) {
        return isOk() ? value : fallback;
    }

    public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        if (!isOk()) {
            return Result.fail(error);
        }
        return Result.ok(mapper.apply(value));
    }

    @Override
    public String toString() {
        return isOk() ? "Result.ok(" + value + ")" : "Result.fail(" + error + ")";
    }
}
