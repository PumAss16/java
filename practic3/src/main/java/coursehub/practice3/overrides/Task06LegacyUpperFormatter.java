package coursehub.practice3.overrides;

public final class Task06LegacyUpperFormatter extends Task06MessageFormatter {

    public String format(Object value) {
        return "legacy-object:" + value;
    }
}
