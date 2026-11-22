package coursehub.practice10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task03Test {

    @Test
    @Timeout(30)
    @DisplayName("allOf results keep the input order")
    void orderIsPreserved() {
        Task3AllOf.Batch batch = Task3AllOf.partial(List.of("C-1", "C-2", "C-3")).join();

        assertTrue(batch.success());
        assertEquals(List.of("C-1", "C-2", "C-3"),
                batch.values().stream().map(Task3AllOf.Loaded::id).toList());
    }

    @Test
    @Timeout(30)
    @DisplayName("partial policy keeps values and reports errors")
    void partialKeepsValuesAndErrors() {
        Task3AllOf.Batch batch = Task3AllOf.partial(List.of("C-1", "bad-2", "C-3")).join();

        assertFalse(batch.success());
        assertEquals(List.of("C-1", "C-3"),
                batch.values().stream().map(Task3AllOf.Loaded::id).toList());
        assertEquals(1, batch.errors().size());
        assertTrue(batch.errors().get(0).contains("bad-2"));
    }

    @Test
    @Timeout(30)
    @DisplayName("fail-fast fails on the first error without waiting for the rest")
    void failFastFailsImmediately() {
        long start = System.nanoTime();
        CompletionException error = assertThrows(CompletionException.class,
                () -> Task3AllOf.failFast(List.of("bad-1", "C-2", "C-3")).join());
        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertInstanceOf(IllegalArgumentException.class, error.getCause());
        assertTrue(elapsed < 400, "fail-fast took " + elapsed + " ms");
    }
}
