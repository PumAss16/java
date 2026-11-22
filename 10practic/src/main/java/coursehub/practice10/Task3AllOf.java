package coursehub.practice10;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class Task3AllOf {

    private Task3AllOf() {
    }

    public record Loaded(String id, String payload) {
    }

    public record Batch(boolean success, List<Loaded> values, List<String> errors) {
    }

    public static CompletableFuture<Loaded> load(String id, long delayMillis) {
        return CompletableFuture.supplyAsync(() -> {
            if (id.startsWith("bad")) {
                throw new IllegalArgumentException("cannot load " + id);
            }
            Task2Pipeline.sleep(delayMillis);
            return new Loaded(id, "payload-" + id);
        });
    }

    public static CompletableFuture<Batch> failFast(List<String> ids) {
        List<CompletableFuture<Loaded>> tasks = new ArrayList<>();
        for (int index = 0; index < ids.size(); index++) {
            tasks.add(load(ids.get(index), delay(ids.size(), index)));
        }
        CompletableFuture<Batch> result = new CompletableFuture<>();
        for (CompletableFuture<Loaded> task : tasks) {
            task.whenComplete((value, error) -> {
                if (error != null && !result.isDone()) {
                    result.completeExceptionally(unwrap(error));
                    tasks.forEach(other -> other.cancel(true));
                }
            });
        }
        CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                .whenComplete((ignored, error) -> {
                    if (error == null) {
                        List<Loaded> values = new ArrayList<>();
                        for (CompletableFuture<Loaded> task : tasks) {
                            values.add(task.join());
                        }
                        result.complete(new Batch(true, List.copyOf(values), List.of()));
                    }
                });
        return result;
    }

    public static CompletableFuture<Batch> partial(List<String> ids) {
        List<CompletableFuture<Loaded>> tasks = new ArrayList<>();
        for (int index = 0; index < ids.size(); index++) {
            tasks.add(load(ids.get(index), delay(ids.size(), index)));
        }
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                .handle((ignored, error) -> {
                    List<Loaded> values = new ArrayList<>();
                    List<String> errors = new ArrayList<>();
                    for (int index = 0; index < tasks.size(); index++) {
                        try {
                            values.add(tasks.get(index).join());
                        } catch (CompletionException e) {
                            errors.add(ids.get(index) + ": " + e.getCause().getMessage());
                        }
                    }
                    return new Batch(errors.isEmpty(), List.copyOf(values), List.copyOf(errors));
                });
    }

    static long delay(int size, int index) {
        return (long) (size - index) * 200L;
    }

    private static Throwable unwrap(Throwable error) {
        if (error instanceof CompletionException && error.getCause() != null) {
            return error.getCause();
        }
        return error;
    }
}
