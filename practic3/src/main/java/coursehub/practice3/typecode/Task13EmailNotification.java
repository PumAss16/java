package coursehub.practice3.typecode;

public final class Task13EmailNotification implements Task13Notification {

    @Override
    public String render(String message) {
        return "EMAIL | " + message;
    }
}
