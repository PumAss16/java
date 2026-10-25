package coursehub.practice3.notify;

public class Task02ConsoleSender implements Task02NotificationSender {

    @Override
    public String send(String message) {
        return "CONSOLE " + message;
    }

    public String send(Integer code) {
        return "CONSOLE-CODE " + code;
    }
}
