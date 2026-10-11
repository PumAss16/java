package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task02Test {

    @Test
    @DisplayName("Дедупликация оставляет порядок первого появления")
    void deduplicateKeepsFirstAppearanceOrder() {
        List<CourseCode> codes = List.of(
                CourseCode.of("cs-101"),
                CourseCode.of("CS-101"),
                CourseCode.of("ma-200"),
                CourseCode.of(" MA-200 "));
        List<CourseCode> unique = CourseCodes.deduplicate(codes);
        assertEquals(List.of(CourseCode.of("CS-101"), CourseCode.of("MA-200")), unique);
        assertEquals(2, unique.size());
    }

    @Test
    @DisplayName("Равные CourseCode дают один элемент в HashSet")
    void equalCourseCodesCollapseInHashSet() {
        Set<CourseCode> codes = new HashSet<>();
        codes.add(CourseCode.of("cs-101"));
        codes.add(CourseCode.of(" CS-101 "));
        assertEquals(1, codes.size());
    }

    @Test
    @DisplayName("Класс без equals и hashCode не схлопывается в HashSet")
    void classWithoutEqualsAndHashCodeKeepsTwoElements() {
        Set<NoEquals> values = new HashSet<>();
        values.add(new NoEquals("cs-101"));
        values.add(new NoEquals("cs-101"));
        assertEquals(2, values.size());
    }

    private static final class NoEquals {

        final String value;

        NoEquals(String value) {
            this.value = value;
        }
    }
}
