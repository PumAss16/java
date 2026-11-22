package collections;

import java.util.LinkedHashSet;
import java.util.List;

final class CourseCodes {

    public static List<CourseCode> deduplicate(List<CourseCode> codes) {
        return List.copyOf(new LinkedHashSet<>(codes));
    }
}
