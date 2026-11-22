package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task08Test {

    private record Money(long cents) implements Comparable<Money> {

        @Override
        public int compareTo(Money other) {
            return Long.compare(cents, other.cents);
        }
    }

    private static class Weight implements Comparable<Weight> {

        private final int grams;

        Weight(int grams) {
            this.grams = grams;
        }

        @Override
        public int compareTo(Weight other) {
            return Integer.compare(grams, other.grams);
        }
    }

    private static class Parcel extends Weight {

        Parcel(int grams) {
            super(grams);
        }
    }

    @Test
    @DisplayName("max находит наибольшее число")
    void maxOfIntegers() {
        assertEquals(9, Generics.max(List.of(4, 9, 2)));
        assertEquals(-1, Generics.max(List.of(-5, -1, -7)));
    }

    @Test
    @DisplayName("max находит наибольшую строку")
    void maxOfStrings() {
        assertEquals("java", Generics.max(List.of("go", "java", "c")));
        assertEquals("b", Generics.max(List.of("b")));
    }

    @Test
    @DisplayName("max сравнивает CourseId по compareTo")
    void maxOfCourseIds() {
        List<CourseId> ids = List.of(CourseId.of("C-5"), CourseId.of("C-7"), CourseId.of("C-2"));
        assertEquals(CourseId.of("C-7"), Generics.max(ids));
    }

    @Test
    @DisplayName("max находит наибольший Money по compareTo")
    void maxOfMoney() {
        assertEquals(new Money(1999), Generics.max(List.of(new Money(999), new Money(1999), new Money(1500))));
    }

    @Test
    @DisplayName("max принимает подтип, сравнимый через базовый тип")
    void maxAcceptsSubtype() {
        Parcel light = new Parcel(100);
        Parcel heavy = new Parcel(900);
        assertSame(heavy, Generics.max(List.of(light, heavy)));
    }

    @Test
    @DisplayName("max на пустом списке бросает NoSuchElementException")
    void maxOnEmptyList() {
        assertThrows(NoSuchElementException.class, () -> Generics.max(List.<CourseId>of()));
        assertThrows(NoSuchElementException.class, () -> Generics.max(null));
    }
}
