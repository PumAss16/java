package coursehub.practice6.domain;

import java.util.Locale;

record CourseTitle(String display, String searchKey) {

    static CourseTitle of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("title is blank: " + raw);
        }
        String display = raw.trim().replaceAll("\\s+", " ");
        return new CourseTitle(display, display.toLowerCase(Locale.ROOT));
    }
}
