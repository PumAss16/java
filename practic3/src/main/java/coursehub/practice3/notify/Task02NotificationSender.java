package coursehub.practice3.notify;

public interface Task02NotificationSender {

    String send(String message);

    default String send(Object payload) {
        return "OBJECT " + payload;
    }
}
