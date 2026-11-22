package coursehub.practice6.strings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task02Test {

    @Test
    @DisplayName("Оба способа сборки дают одинаковый текст и длину")
    void bothBuildersProduceEqualText() {
        List<String> lines = courseLines(10_000);
        String plus = ReportBuilder.withPlus(lines);
        String builder = ReportBuilder.withBuilder(lines);
        assertEquals(builder, plus);
        assertEquals(builder.length(), plus.length());
        assertEquals(10_000, countLines(plus));
        assertEquals(10_000, countLines(builder));
    }

    @Test
    @DisplayName("Грубый замер выводит наносекунды для обоих методов")
    void timingPrintsNanosecondsForBothMethods() {
        List<String> warmup = courseLines(1_000);
        List<String> lines = courseLines(10_000);
        ReportBuilder.withPlus(warmup);
        ReportBuilder.withBuilder(warmup);

        long startPlus = System.nanoTime();
        String plus = ReportBuilder.withPlus(lines);
        long plusNanos = System.nanoTime() - startPlus;

        long startBuilder = System.nanoTime();
        String builder = ReportBuilder.withBuilder(lines);
        long builderNanos = System.nanoTime() - startBuilder;

        System.out.printf("withPlus: %d ns%n", plusNanos);
        System.out.printf("withBuilder: %d ns%n", builderNanos);
        assertEquals(builder, plus);
        assertEquals(plus.length(), builder.length());
        assertEquals(10_000, countLines(plus));
        assertEquals(10_000, countLines(builder));
    }

    @Test
    @DisplayName("Исходный список строк не меняется")
    void sourceListIsNotModified() {
        List<String> lines = new ArrayList<>(courseLines(10));
        List<String> snapshot = new ArrayList<>(lines);
        ReportBuilder.withPlus(lines);
        ReportBuilder.withBuilder(lines);
        assertEquals(snapshot, lines);
        assertEquals(10, lines.size());
    }

    private static List<String> courseLines(int count) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lines.add(String.format("course-%04d", i));
        }
        return lines;
    }

    private static int countLines(String text) {
        return text.split("\n", -1).length - 1;
    }
}
