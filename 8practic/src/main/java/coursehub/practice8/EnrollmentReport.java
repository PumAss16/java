package coursehub.practice8;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record EnrollmentReport(
        List<String> uniqueStudents,
        Map<String, Double> completionRateByCourse,
        List<CourseCompletion> top3,
        Map<Integer, Long> enrollmentsByLevel,
        List<Anomaly> anomalies,
        long anomalyCount,
        long eventsRead) {

    public EnrollmentReport {
        uniqueStudents = List.copyOf(uniqueStudents);
        completionRateByCourse = Collections.unmodifiableMap(
                new LinkedHashMap<>(completionRateByCourse));
        top3 = List.copyOf(top3);
        enrollmentsByLevel = Collections.unmodifiableMap(
                new LinkedHashMap<>(enrollmentsByLevel));
        anomalies = List.copyOf(anomalies);
        if (top3.size() > 3) {
            throw new IllegalArgumentException("top3 must contain at most three items");
        }
        if (anomalyCount < anomalies.size()) {
            throw new IllegalArgumentException("anomalyCount must cover the sample");
        }
        if (anomalyCount < 0 || eventsRead < 0) {
            throw new IllegalArgumentException("counters must be >= 0");
        }
    }
}
