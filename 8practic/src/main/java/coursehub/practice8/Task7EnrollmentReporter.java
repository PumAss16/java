package coursehub.practice8;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Task7EnrollmentReporter {

    private static final int MAX_ANOMALIES = 100;

    private Task7EnrollmentReporter() {
    }

    public static EnrollmentReport report(Stream<EnrollmentEvent> events, List<Course> courses) {
        Objects.requireNonNull(events, "events");
        Objects.requireNonNull(courses, "courses");

        Map<String, Course> courseByCode = courses.stream().collect(Collectors.toMap(
                Course::code,
                Function.identity(),
                (left, right) -> left,
                LinkedHashMap::new));

        Map<String, Set<String>> enrolled = new TreeMap<>();
        Map<String, Set<String>> completed = new TreeMap<>();
        Set<String> uniqueStudents = new TreeSet<>();
        List<Anomaly> anomalies = new ArrayList<>();

        long eventsRead = 0;
        long anomalyCount = 0;
        long index = 0;

        Iterator<EnrollmentEvent> iterator = events.iterator();
        while (iterator.hasNext()) {
            EnrollmentEvent event = iterator.next();
            index++;
            eventsRead++;

            if (event == null) {
                anomalyCount = addAnomaly(anomalies, anomalyCount, index, null, null, "null event");
                continue;
            }

            String student = event.studentId();
            String code = event.courseCode();

            if (student == null || student.isBlank()) {
                anomalyCount = addAnomaly(
                        anomalies, anomalyCount, index, student, code, "blank student id");
                continue;
            }

            Course course = courseByCode.get(code);
            if (course == null) {
                anomalyCount = addAnomaly(
                        anomalies, anomalyCount, index, student, code, "unknown course");
                continue;
            }

            uniqueStudents.add(student);

            switch (event.type()) {
                case ENROLLED -> {
                    Set<String> students = enrolled.computeIfAbsent(code, key -> new TreeSet<>());
                    if (!students.add(student)) {
                        anomalyCount = addAnomaly(
                                anomalies, anomalyCount, index, student, code, "duplicate enrollment");
                    }
                }
                case COMPLETED -> {
                    Set<String> enrolledStudents = enrolled.getOrDefault(code, Set.of());
                    if (!enrolledStudents.contains(student)) {
                        anomalyCount = addAnomaly(
                                anomalies, anomalyCount, index, student, code,
                                "completion without enrollment");
                        continue;
                    }
                    Set<String> completedStudents =
                            completed.computeIfAbsent(code, key -> new TreeSet<>());
                    if (!completedStudents.add(student)) {
                        anomalyCount = addAnomaly(
                                anomalies, anomalyCount, index, student, code,
                                "duplicate completion");
                    }
                }
                case CANCELLED -> {
                }
            }
        }

        List<CourseCompletion> completions = courseByCode.keySet().stream()
                .sorted()
                .map(code -> {
                    int enrolledCount = enrolled.getOrDefault(code, Set.of()).size();
                    int completedCount = completed.getOrDefault(code, Set.of()).size();
                    double rate = enrolledCount == 0
                            ? 0.0
                            : (double) completedCount / enrolledCount;
                    return new CourseCompletion(code, enrolledCount, completedCount, rate);
                })
                .toList();

        Map<String, Double> rates = new LinkedHashMap<>();
        for (CourseCompletion completion : completions) {
            rates.put(completion.courseCode(), completion.completionRate());
        }

        List<CourseCompletion> top3 = completions.stream()
                .filter(completion -> completion.enrolled() > 0)
                .sorted(Comparator
                        .comparingDouble(CourseCompletion::completionRate).reversed()
                        .thenComparing(CourseCompletion::completed, Comparator.reverseOrder())
                        .thenComparing(CourseCompletion::courseCode))
                .limit(3)
                .toList();

        Map<Integer, Long> byLevel = new TreeMap<>();
        for (Map.Entry<String, Set<String>> entry : enrolled.entrySet()) {
            int count = entry.getValue().size();
            if (count == 0) {
                continue;
            }
            Course course = courseByCode.get(entry.getKey());
            byLevel.merge(course.level(), (long) count, Long::sum);
        }

        return new EnrollmentReport(
                List.copyOf(uniqueStudents),
                rates,
                top3,
                byLevel,
                anomalies,
                anomalyCount,
                eventsRead);
    }

    private static long addAnomaly(List<Anomaly> anomalies, long count, long index,
                                   String student, String code, String reason) {
        if (anomalies.size() < MAX_ANOMALIES) {
            anomalies.add(new Anomaly(index, student, code, reason));
        }
        return count + 1;
    }
}
