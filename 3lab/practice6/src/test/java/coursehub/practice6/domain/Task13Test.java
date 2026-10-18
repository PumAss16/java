package coursehub.practice6.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task13Test {

    @Test
    @DisplayName("Название нормализуется")
    void normalizesDisplayAndSearchKey() {
        CourseTitle title = CourseTitle.of("  Java   Basics  ");
        assertEquals("Java Basics", title.display());
        assertEquals("java basics", title.searchKey());
    }

    @Test
    @DisplayName("Разные строки дают один ключ поиска")
    void differentRawTitlesShareSearchKey() {
        CourseTitle first = CourseTitle.of("Java Basics");
        CourseTitle second = CourseTitle.of(" java  basics ");
        assertEquals(first.searchKey(), second.searchKey());
        assertNotEquals(first.display(), second.display());
    }

    @Test
    @DisplayName("Ключ поиска не зависит от локали")
    void searchKeyUsesRootLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr"));
            assertEquals("title i", CourseTitle.of("TITLE I").searchKey());
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    @DisplayName("Пустое название отклоняется")
    void rejectsBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> CourseTitle.of(null));
        assertThrows(IllegalArgumentException.class, () -> CourseTitle.of("   "));
        assertThrows(IllegalArgumentException.class, () -> CourseTitle.of(" \t\n "));
    }
}
