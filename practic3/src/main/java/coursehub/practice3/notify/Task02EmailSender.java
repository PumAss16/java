package coursehub.practice3.notify;

public class Task02EmailSender implements Task02NotificationSender {

    @Override
    public String send(String message) {
        return "EMAIL " + message;
    }

    public String send(Integer code) {
        return "EMAIL-CODE " + code;
    }

    @Override
    public String send(Object payload) {
        return "EMAIL-OBJECT " + payload;
    }
}
