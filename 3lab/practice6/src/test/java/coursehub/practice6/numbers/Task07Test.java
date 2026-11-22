package coursehub.practice6.numbers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Task07Test {
    @Test
    @DisplayName("Одинаковый seed даёт одинаковую выборку")
    void sameSeedGivesSameSample() {
        assertArrayEquals(
                RandomSampleService.seeded(20, 0, 100, 42L),
                RandomSampleService.seeded(20, 0, 100, 42L)
        );
    }

    @Test
    @DisplayName("Разные seed дают разные выборки")
    void differentSeedsGiveDifferentSamples() {
        double[] first = RandomSampleService.seeded(20, 0, 100, 1L);
        double[] second = RandomSampleService.seeded(20, 0, 100, 2L);
        assertFalse(Arrays.equals(first, second));
    }

    @Test
    @DisplayName("Значения seeded попадают в диапазон")
    void seededValuesStayInRange() {
        checkRange(RandomSampleService.seeded(100, -5, 5, 7L), -5, 5);
    }

    @Test
    @DisplayName("Значения production попадают в диапазон")
    void productionValuesStayInRange() {
        checkRange(RandomSampleService.production(100, 10, 20), 10, 20);
    }

    @Test
    @DisplayName("Статистика [1, 2, 3, 4] считается верно")
    void statsForSimpleSample() {
        SampleStats stats = SampleStats.of(new double[] {1, 2, 3, 4});
        assertEquals(4, stats.count());
        assertEquals(2.5, stats.mean());
        assertEquals(1.0, stats.min());
        assertEquals(4.0, stats.max());
        assertEquals(1.118033988749895, stats.stdDev(), 1e-9);
    }

    @Test
    @DisplayName("Среднее округляется до двух знаков")
    void meanIsRounded() {
        SampleStats stats = SampleStats.of(new double[] {1, 2, 2, 3});
        assertEquals(2.0, stats.meanRounded(2));
    }

    @Test
    @DisplayName("Пустые значения запрещены")
    void emptyValuesRejected() {
        assertThrows(IllegalArgumentException.class, () -> SampleStats.of(null));
        assertThrows(IllegalArgumentException.class, () -> SampleStats.of(new double[0]));
    }

    @Test
    @DisplayName("Некорректный размер или диапазон запрещён")
    void invalidSizeOrRangeRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> RandomSampleService.seeded(0, 0, 10, 1L)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> RandomSampleService.seeded(5, 10, 9, 1L)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> RandomSampleService.production(-1, 0, 10)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> RandomSampleService.production(5, 5, 4)
        );
    }

    private static void checkRange(double[] values, int min, int max) {
        for (double value : values) {
            assertTrue(value >= min && value <= max);
        }
    }
}
