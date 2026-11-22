package coursehub.practice10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task02Test {

    @Test
    @Timeout(30)
    @DisplayName("supplyAsync and thenApply run on the provided executor")
    void stagesRunOnProvidedExecutor() throws Exception {
        ExecutorService pool = Task2Pipeline.newPool("card");
        try {
            Task2Pipeline.Trace trace = new Task2Pipeline.Trace();
            String card = Task2Pipeline.chain(pool, "JAVA-101", trace).get(5, TimeUnit.SECONDS);

            assertEquals("CARD: JAVA-101 | КУРС JAVA-101 | LEVEL 2", card);
            assertEquals(4, trace.entries().size());
            assertTrue(trace.entries().stream().allMatch(entry -> entry.contains("@ card-")));
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("thenCompose returns a flat future")
    void thenComposeReturnsFlatFuture() throws Exception {
        ExecutorService pool = Task2Pipeline.newPool("card");
        try {
            Task2Pipeline.CourseCard card = Task2Pipeline.loadCard(pool, "JAVA-101", "S-1")
                    .get(5, TimeUnit.SECONDS);

            assertEquals("JAVA-101", card.code());
            assertEquals("S-1", card.studentId());
            assertEquals(List.of("JAVA-101", "DB-201"), card.courses());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("thenApply keeps a nested future, thenCompose flattens it")
    void thenApplyDoesNotFlatten() throws Exception {
        ExecutorService pool = Task2Pipeline.newPool("card");
        try {
            CompletableFuture<CompletableFuture<List<Task2Pipeline.Enrollment>>> nested =
                    Task2Pipeline.loadCourse(pool, "JAVA-101", 10)
                            .thenApply(course -> Task2Pipeline.loadEnrollments(pool, "S-1"));
            assertInstanceOf(CompletableFuture.class, nested.join());

            CompletableFuture<List<Task2Pipeline.Enrollment>> flat =
                    Task2Pipeline.loadCourse(pool, "JAVA-101", 10)
                            .thenCompose(course -> Task2Pipeline.loadEnrollments(pool, "S-1"));
            assertInstanceOf(List.class, flat.join());
        } finally {
            pool.shutdownNow();
        }
    }
}
