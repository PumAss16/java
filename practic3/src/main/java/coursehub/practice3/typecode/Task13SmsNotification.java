package coursehub.practice3.typecode;

public final class Task13SmsNotification implements Task13Notification {

    private static final int MAX_LENGTH = 70;

    @Override
    public String render(String message) {
        String text = message.length() <= MAX_LENGTH
                ? message
                : message.substring(0, MAX_LENGTH - 3) + "...";
        return "SMS | " + text;
    }
}
