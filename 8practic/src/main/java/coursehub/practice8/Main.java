package coursehub.practice8;

import java.util.List;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        List<Course> courses = courses();

        System.out.println("1. " + Task1Sorter.sort(courses).stream()
                .map(Course::code).toList());

        Task2TopicIndex.Result topics = Task2TopicIndex.build(courses);
        System.out.println("2. " + topics.topics());
        System.out.println("   " + topics.byTopic().keySet());

        System.out.println("3. " + Task3Totals.sum(courses));
        System.out.println("   wrong identity: "
                + Task3Totals.sum(courses, Task3Totals.wrongIdentity()));

        System.out.println("4. " + Task4LevelStatistics.summarize(courses));

        System.out.println("5. " + Task5LongestCourse.titleOrFallback(courses, "none"));
        System.out.println("   " + Task5LongestCourse.titleOrFallback(List.of(), "none"));

        Task6CourseStats.Stats stats =
                courses.stream().collect(Task6CourseStats.collector());
        System.out.println("6. " + stats + " avg=" + stats.average());

        EnrollmentReport report = Task7EnrollmentReporter.report(
                events().stream(), courses);
        System.out.println("7. students=" + report.uniqueStudents());
        System.out.println("   rates=" + report.completionRateByCourse());
        System.out.println("   top3=" + report.top3());
        System.out.println("   levels=" + report.enrollmentsByLevel());
        System.out.println("   anomalies=" + report.anomalies());
    }

    private static List<Course> courses() {
        return List.of(
                new Course("C-1", "Java", 1, 90,
                        new Module("Basics", List.of(" Java ", "Collections")),
                        new Module("Advanced", List.of("Streams", "java"))),
                new Course("C-2", "SQL", 1, 60,
                        new Module("Queries", List.of("SQL"))),
                new Course("C-3", "Go", 2, 120,
                        new Module("Concurrency", List.of("Goroutines"))),
                new Course("C-4", "Kotlin", 2, 60,
                        new Module("JVM", List.of("Kotlin", "Java"))));
    }

    private static List<EnrollmentEvent> events() {
        return List.of(
                new EnrollmentEvent("S-1", "C-1", EventType.ENROLLED),
                new EnrollmentEvent("S-2", "C-1", EventType.ENROLLED),
                new EnrollmentEvent("S-1", "C-1", EventType.COMPLETED),
                new EnrollmentEvent("S-2", "C-1", EventType.COMPLETED),
                new EnrollmentEvent("S-1", "C-2", EventType.ENROLLED),
                new EnrollmentEvent("S-1", "C-2", EventType.COMPLETED),
                new EnrollmentEvent("S-3", "C-3", EventType.ENROLLED),
                new EnrollmentEvent("S-4", "C-1", EventType.COMPLETED),
                new EnrollmentEvent(" ", "C-1", EventType.ENROLLED),
                new EnrollmentEvent("S-5", "C-9", EventType.ENROLLED));
    }
}
