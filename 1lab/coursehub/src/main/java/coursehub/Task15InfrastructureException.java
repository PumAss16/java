package coursehub;

public class Task15InfrastructureException extends RuntimeException {

    public Task15InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public String code() {
        return "CH-4001";
    }
}
