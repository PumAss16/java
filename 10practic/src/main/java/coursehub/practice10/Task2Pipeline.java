package coursehub.practice10;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class Task2Pipeline {

    private Task2Pipeline() {
    }

    public record Course(String code, String title, int level) {
    }

    public record Enrollment(String studentId, String courseCode) {
    }

    public record CourseCard(String code, String title, int level, String studentId, List<String> courses) {
    }

    public static final class Trace {
        private final List<String> entries = Collections.synchronizedList(new ArrayList<>());

        public void add(String stage) {
            entries.add(stage + " @ " + Thread.currentThread().getName());
        }

        public List<String> entries() {
            synchronized (entries) {
                return List.copyOf(entries);
            }
        }
    }

    public static ExecutorService newPool(String prefix) {
        return Executors.newFixedThreadPool(2, namedThreads(prefix));
    }

    public static CompletableFuture<Course> loadCourse(Executor executor, String code, long delayMillis) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(delayMillis);
            return new Course(code, "Курс " + code, 2);
        }, executor);
    }

    public static CompletableFuture<String> chain(Executor executor, String code, Trace trace) {
        return CompletableFuture.supplyAsync(() -> {
                    trace.add("load");
                    sleep(20);
                    return new Course(code, "Курс " + code, 2);
                }, executor)
                .thenApply(course -> {
                    trace.add("format");
                    return course.code() + " | " + course.title() + " | level " + course.level();
                })
                .thenApply(text -> {
                    trace.add("uppercase");
                    return text.toUpperCase();
                })
                .thenApply(text -> {
                    trace.add("accept");
                    return "CARD: " + text;
                });
    }

    public static CompletableFuture<List<Enrollment>> loadEnrollments(Executor executor, String studentId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(20);
            return List.of(new Enrollment(studentId, "JAVA-101"), new Enrollment(studentId, "DB-201"));
        }, executor);
    }

    public static CompletableFuture<CourseCard> loadCard(Executor executor, String code, String studentId) {
        return loadCourse(executor, code, 20).thenCompose(course ->
                loadEnrollments(executor, studentId).thenApply(enrollments ->
                        new CourseCard(course.code(), course.title(), course.level(), studentId,
                                enrollments.stream().map(Enrollment::courseCode).toList())));
    }

    public static CompletableFuture<CompletableFuture<List<Enrollment>>> loadEnrollmentsNested(
            Executor executor, String studentId) {
        return loadCourse(executor, "JAVA-101", 20)
                .thenApply(course -> loadEnrollments(executor, studentId));
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    public static ThreadFactory namedThreads(String prefix) {
        AtomicInteger sequence = new AtomicInteger();
        return runnable -> new Thread(runnable, prefix + "-" + sequence.incrementAndGet());
    }
}
