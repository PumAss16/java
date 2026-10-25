package coursehub.practice3.typecode;

public final class Task13PushNotification implements Task13Notification {

    @Override
    public String render(String message) {
        return "PUSH | " + message;
    }
}
