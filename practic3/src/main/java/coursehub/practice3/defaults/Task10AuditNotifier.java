package coursehub.practice3.defaults;

public final class Task10AuditNotifier implements Task10Auditable, Task10Notifiable {

    @Override
    public String describe() {
        return Task10Auditable.super.describe() + " + " + Task10Notifiable.super.describe();
    }
}
