package coursehub.practice8;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class Task2TopicIndex {

    private static final Comparator<Course> COURSE_ORDER = Comparator
            .comparing(Course::code)
            .thenComparing(Course::title);

    private Task2TopicIndex() {
    }

    public static String normalize(String topic) {
        Objects.requireNonNull(topic, "topic");
        return topic.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    public static Result build(List<Course> courses) {
        Objects.requireNonNull(courses, "courses");

        List<String> topics = courses.stream()
                .flatMap(course -> course.modules().stream())
                .flatMap(module -> module.topics().stream())
                .map(Task2TopicIndex::normalize)
                .filter(topic -> !topic.isEmpty())
                .distinct()
                .sorted()
                .toList();

        Map<String, List<Course>> byTopic = courses.stream()
                .flatMap(course -> course.modules().stream()
                        .flatMap(module -> module.topics().stream())
                        .map(Task2TopicIndex::normalize)
                        .filter(topic -> !topic.isEmpty())
                        .distinct()
                        .map(topic -> Map.entry(topic, course)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        TreeMap::new,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .distinct()
                                                .sorted(COURSE_ORDER)
                                                .toList()))));

        return new Result(topics, byTopic);
    }

    public record Result(List<String> topics, Map<String, List<Course>> byTopic) {

        public Result {
            topics = List.copyOf(topics);
            byTopic = Collections.unmodifiableMap(new LinkedHashMap<>(byTopic));
        }
    }
}
