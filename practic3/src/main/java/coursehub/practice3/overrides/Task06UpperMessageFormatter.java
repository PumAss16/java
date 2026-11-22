package coursehub.practice3.overrides;

import java.util.Locale;

public final class Task06UpperMessageFormatter extends Task06MessageFormatter {

    @Override
    public String format(String value) {
        return "upper:" + value.toUpperCase(Locale.ROOT);
    }
}
