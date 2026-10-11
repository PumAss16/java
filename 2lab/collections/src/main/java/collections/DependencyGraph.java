package collections;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DependencyGraph<T> {

    private final Map<T, Set<T>> prerequisites = new LinkedHashMap<>();

    public void addVertex(T vertex) {
        Objects.requireNonNull(vertex, "vertex");
        prerequisites.computeIfAbsent(vertex, key -> new LinkedHashSet<>());
    }

    public void addEdge(T prerequisite, T course) {
        Objects.requireNonNull(prerequisite, "prerequisite");
        Objects.requireNonNull(course, "course");
        requireVertex(prerequisite);
        requireVertex(course);
        prerequisites.get(course).add(prerequisite);
    }

    public Set<T> prerequisitesOf(T course) {
        Objects.requireNonNull(course, "course");
        requireVertex(course);
        return Collections.unmodifiableSet(new LinkedHashSet<>(prerequisites.get(course)));
    }

    public List<T> studyOrder() {
        Map<T, Integer> remaining = new LinkedHashMap<>();
        Map<T, List<T>> dependents = new LinkedHashMap<>();
        for (T vertex : prerequisites.keySet()) {
            remaining.put(vertex, prerequisites.get(vertex).size());
            dependents.put(vertex, new ArrayList<>());
        }
        for (Map.Entry<T, Set<T>> entry : prerequisites.entrySet()) {
            for (T prerequisite : entry.getValue()) {
                dependents.get(prerequisite).add(entry.getKey());
            }
        }
        Deque<T> ready = new ArrayDeque<>();
        for (Map.Entry<T, Integer> entry : remaining.entrySet()) {
            if (entry.getValue() == 0) {
                ready.addLast(entry.getKey());
            }
        }
        List<T> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            T vertex = ready.pollFirst();
            order.add(vertex);
            for (T dependent : dependents.get(vertex)) {
                int left = remaining.merge(dependent, -1, Integer::sum);
                if (left == 0) {
                    ready.addLast(dependent);
                }
            }
        }
        if (order.size() != prerequisites.size()) {
            throw new IllegalStateException(cycleMessage(findCycle()));
        }
        return List.copyOf(order);
    }

    public List<T> findCycle() {
        Map<T, Integer> color = new LinkedHashMap<>();
        for (T vertex : prerequisites.keySet()) {
            color.put(vertex, 0);
        }
        Deque<T> path = new ArrayDeque<>();
        for (T vertex : prerequisites.keySet()) {
            List<T> cycle = visit(vertex, color, path);
            if (!cycle.isEmpty()) {
                return cycle;
            }
        }
        return List.of();
    }

    public int vertexCount() {
        return prerequisites.size();
    }

    public int edgeCount() {
        int total = 0;
        for (Set<T> incoming : prerequisites.values()) {
            total += incoming.size();
        }
        return total;
    }

    private List<T> visit(T vertex, Map<T, Integer> color, Deque<T> path) {
        color.put(vertex, 1);
        path.addLast(vertex);
        for (T next : prerequisites.get(vertex)) {
            int state = color.get(next);
            if (state == 1) {
                return buildCycle(path, next);
            }
            if (state == 0) {
                List<T> cycle = visit(next, color, path);
                if (!cycle.isEmpty()) {
                    return cycle;
                }
            }
        }
        path.removeLast();
        color.put(vertex, 2);
        return List.of();
    }

    private List<T> buildCycle(Deque<T> path, T start) {
        List<T> cycle = new ArrayList<>();
        for (T vertex : path) {
            if (!cycle.isEmpty() || vertex.equals(start)) {
                cycle.add(vertex);
            }
        }
        cycle.add(start);
        return cycle;
    }

    private String cycleMessage(List<T> cycle) {
        StringBuilder message = new StringBuilder("cycle: ");
        for (int i = 0; i < cycle.size(); i++) {
            if (i > 0) {
                message.append(" -> ");
            }
            message.append(cycle.get(i));
        }
        return message.toString();
    }

    private void requireVertex(T vertex) {
        if (!prerequisites.containsKey(vertex)) {
            throw new IllegalArgumentException("unknown vertex: " + vertex);
        }
    }
}
