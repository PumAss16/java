package coursehub.practice3.notify;

public final class Task04ConsoleNotificationSender extends Task04AbstractNotificationSender {

    @Override
    protected String address() {
        return "console";
    }

    @Override
    protected String doSend(String address, String message) {
        return "CONSOLE [" + address + "]: " + message;
    }
}
