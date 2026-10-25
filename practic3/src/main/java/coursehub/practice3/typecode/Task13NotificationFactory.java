package coursehub.practice3.typecode;

public final class Task13NotificationFactory {

    public Task13Notification create(String type) {
        return switch (type) {
            case "email" -> new Task13EmailNotification();
            case "sms" -> new Task13SmsNotification();
            case "push" -> new Task13PushNotification();
            default -> throw new IllegalArgumentException("unknown notification type: " + type);
        };
    }
}
