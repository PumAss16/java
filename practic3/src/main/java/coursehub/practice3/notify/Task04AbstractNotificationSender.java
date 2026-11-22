package coursehub.practice3.notify;

public abstract class Task04AbstractNotificationSender implements Task02NotificationSender {

    @Override
    public final String send(String message) {
        String address = address();
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address must not be blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        return doSend(address, message.strip());
    }

    @Override
    public final String send(Object payload) {
        return send(String.valueOf(payload));
    }

    protected abstract String address();

    protected abstract String doSend(String address, String message);
}
