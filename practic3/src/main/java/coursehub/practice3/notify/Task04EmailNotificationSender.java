package coursehub.practice3.notify;

public final class Task04EmailNotificationSender extends Task04AbstractNotificationSender {

    private final String address;

    public Task04EmailNotificationSender(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address must not be blank");
        }
        this.address = address;
    }

    @Override
    protected String address() {
        return address;
    }

    @Override
    protected String doSend(String address, String message) {
        return "EMAIL to " + address + ": " + message;
    }
}
