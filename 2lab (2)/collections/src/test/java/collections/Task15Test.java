package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task15Test {

    private static DependencyGraph<String> acyclicGraph() {
        DependencyGraph<String> graph = new DependencyGraph<>();
        for (String vertex : List.of("A", "B", "C", "D", "E")) {
            graph.addVertex(vertex);
        }
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");
        graph.addEdge("D", "E");
        return graph;
    }

    @Test
    @DisplayName("ациклический граф даёт детерминированный порядок")
    void acyclicGraphHasDeterministicOrder() {
        DependencyGraph<String> graph = acyclicGraph();
        List<String> expected = List.of("A", "B", "C", "D", "E");
        List<String> order = graph.studyOrder();
        assertEquals(expected, order);
        assertEquals(expected, graph.studyOrder());
        for (int i = 0; i < expected.size(); i++) {
            for (String prerequisite : graph.prerequisitesOf(expected.get(i))) {
                assertTrue(order.indexOf(prerequisite) < i);
            }
        }
        assertEquals(List.of(), graph.findCycle());
        assertEquals(5, graph.vertexCount());
        assertEquals(5, graph.edgeCount());
        assertThrows(UnsupportedOperationException.class, () -> graph.prerequisitesOf("D").add("A"));
    }

    @Test
    @DisplayName("цикл обнаруживается и диагностируется")
    void cycleIsDetected() {
        DependencyGraph<String> graph = new DependencyGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");
        IllegalStateException error = assertThrows(IllegalStateException.class, graph::studyOrder);
        assertTrue(error.getMessage().contains("cycle"));
        List<String> cycle = graph.findCycle();
        assertFalse(cycle.isEmpty());
        assertEquals(cycle.get(0), cycle.get(cycle.size() - 1));
        assertTrue(cycle.containsAll(List.of("A", "B", "C")));
        assertEquals(cycle, graph.findCycle());
    }

    @Test
    @DisplayName("рёбра требуют объявленных вершин")
    void edgesRequireDeclaredVertices() {
        DependencyGraph<String> graph = new DependencyGraph<>();
        graph.addVertex("A");
        IllegalArgumentException missingCourse =
                assertThrows(IllegalArgumentException.class, () -> graph.addEdge("A", "B"));
        assertTrue(missingCourse.getMessage().contains("B"));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("Z", "A"));
        assertThrows(IllegalArgumentException.class, () -> graph.prerequisitesOf("B"));
        assertThrows(NullPointerException.class, () -> graph.addVertex(null));
        assertThrows(NullPointerException.class, () -> graph.addEdge(null, "A"));
    }

    @Test
    @DisplayName("пустой граф и одиночная вершина")
    void emptyAndSingleVertexGraphs() {
        DependencyGraph<String> empty = new DependencyGraph<>();
        assertEquals(List.of(), empty.studyOrder());
        assertEquals(List.of(), empty.findCycle());
        assertEquals(0, empty.vertexCount());
        assertEquals(0, empty.edgeCount());

        DependencyGraph<String> single = new DependencyGraph<>();
        single.addVertex("A");
        single.addVertex("A");
        assertEquals(List.of("A"), single.studyOrder());
        assertEquals(1, single.vertexCount());
        assertEquals(0, single.edgeCount());
    }

    @Test
    @DisplayName("граф работает с CourseId")
    void graphWorksWithCourseId() {
        DependencyGraph<CourseId> graph = new DependencyGraph<>();
        CourseId basic = CourseId.of("C-1");
        CourseId advanced = CourseId.of("C-2");
        graph.addVertex(basic);
        graph.addVertex(advanced);
        graph.addEdge(basic, advanced);
        assertEquals(List.of(basic, advanced), graph.studyOrder());
        assertEquals(Set.of(basic), graph.prerequisitesOf(advanced));
        assertEquals(List.of(), graph.findCycle());
        assertEquals(2, graph.vertexCount());
        assertEquals(1, graph.edgeCount());
    }
}
