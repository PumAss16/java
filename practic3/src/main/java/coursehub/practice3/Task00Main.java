package coursehub.practice3;

import coursehub.practice3.defaults.Task10AuditNotifier;
import coursehub.practice3.design.Task05BaseEntity;
import coursehub.practice3.design.Task05EnrollmentWindow;
import coursehub.practice3.design.Task05Formatter;
import coursehub.practice3.design.Task05Identifiable;
import coursehub.practice3.design.Task05Instructor;
import coursehub.practice3.domain.Task00Course;
import coursehub.practice3.domain.Task00EnrollmentRequest;
import coursehub.practice3.domain.Task00Student;
import coursehub.practice3.enrollment.Task09AgePolicy;
import coursehub.practice3.enrollment.Task09CapacityPolicy;
import coursehub.practice3.enrollment.Task12EnrollmentResult;
import coursehub.practice3.enrollment.Task09EnrollmentService;
import coursehub.practice3.enrollment.Task09ManualReviewPolicy;
import coursehub.practice3.enrollment.Task09PrerequisitePolicy;
import coursehub.practice3.equality.Task11ColoredPoint;
import coursehub.practice3.equality.Task11ComposedColoredPoint;
import coursehub.practice3.equality.Task11Point;
import coursehub.practice3.equality.Task11PointValue;
import coursehub.practice3.equality.Task11StrictColoredPoint;
import coursehub.practice3.equality.Task11StrictPoint;
import coursehub.practice3.format.Task01CompactCourseFormatter;
import coursehub.practice3.format.Task01CourseFormatter;
import coursehub.practice3.format.Task01DetailedCourseFormatter;
import coursehub.practice3.format.Task03MarkdownCourseFormatter;
import coursehub.practice3.notify.Task04ConsoleNotificationSender;
import coursehub.practice3.notify.Task02ConsoleSender;
import coursehub.practice3.notify.Task04EmailNotificationSender;
import coursehub.practice3.notify.Task02EmailSender;
import coursehub.practice3.notify.Task07FlakySender;
import coursehub.practice3.notify.Task07LoggingEmailSender;
import coursehub.practice3.notify.Task07LoggingSender;
import coursehub.practice3.notify.Task07MetricsSender;
import coursehub.practice3.notify.Task02NotificationSender;
import coursehub.practice3.notify.Task07RetrySender;
import coursehub.practice3.offering.Task14ClassroomFormat;
import coursehub.practice3.offering.Task14CourseOffering;
import coursehub.practice3.offering.Task14FixedPayment;
import coursehub.practice3.offering.Task14FreePayment;
import coursehub.practice3.offering.Task14HybridFormat;
import coursehub.practice3.offering.Task14InstallmentPayment;
import coursehub.practice3.offering.Task14OnlineFormat;
import coursehub.practice3.offering.Task14SelfPacedFormat;
import coursehub.practice3.offering.Task14SubscriptionPayment;
import coursehub.practice3.overrides.Task06LegacyUpperFormatter;
import coursehub.practice3.overrides.Task06MessageFormatter;
import coursehub.practice3.overrides.Task06UpperMessageFormatter;
import coursehub.practice3.repository.Task08CourseCatalogView;
import coursehub.practice3.repository.Task08CourseReader;
import coursehub.practice3.repository.Task08InMemoryCourseRepository;
import coursehub.practice3.repository.Task08ReadOnlyCourseRepository;
import coursehub.practice3.rules.Task15AgeRule;
import coursehub.practice3.rules.Task15AllOfRule;
import coursehub.practice3.rules.Task15AnyOfRule;
import coursehub.practice3.rules.Task15CapacityRule;
import coursehub.practice3.rules.Task15DuplicateEnrollmentRule;
import coursehub.practice3.rules.Task15EnrollmentEngine;
import coursehub.practice3.rules.Task15EnrollmentRule;
import coursehub.practice3.rules.Task15PrerequisitesRule;
import coursehub.practice3.rules.Task15RuleResult;
import coursehub.practice3.rules.Task15ScheduleConflictRule;
import coursehub.practice3.typecode.Task13Notification;
import coursehub.practice3.typecode.Task13NotificationFactory;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class Task00Main {

    private Task00Main() {
    }

    public static void main(String[] args) {
        task1();
        task2();
        task3();
        task4();
        task5();
        task6();
        task7();
        task8();
        task9();
        task10();
        task11();
        task12();
        task13();
        task14();
        task15();
    }

    private static void task1() {
        System.out.println("== 1. CourseFormatter ==");
        Task00Course course = new Task00Course("C-1", "Java");
        Task01CourseFormatter compact = new Task01CompactCourseFormatter();
        Task01CourseFormatter detailed = new Task01DetailedCourseFormatter();
        System.out.println(compact.format(course));
        System.out.println(detailed.format(course));
    }

    private static void task2() {
        System.out.println("== 2. overload and override ==");
        Task02NotificationSender email = new Task02EmailSender();
        Task02NotificationSender console = new Task02ConsoleSender();
        Task02EmailSender concreteEmail = new Task02EmailSender();
        Task02ConsoleSender concreteConsole = new Task02ConsoleSender();
        System.out.println(email.send("hello"));
        System.out.println(email.send(1));
        System.out.println(email.send((Object) "hello"));
        System.out.println(concreteEmail.send(1));
        System.out.println(console.send(1));
        System.out.println(concreteConsole.send(1));
        System.out.println(console.send("hello"));
    }

    private static void task3() {
        System.out.println("== 3. polymorphic array ==");
        Task00Course course = new Task00Course("C-1", "Java");
        Task01CourseFormatter[] formatters = {
                new Task01CompactCourseFormatter(),
                new Task01DetailedCourseFormatter(),
                new Task03MarkdownCourseFormatter()
        };
        for (Task01CourseFormatter formatter : formatters) {
            System.out.println(formatter.format(course));
        }
    }

    private static void task4() {
        System.out.println("== 4. abstract sender ==");
        Task02NotificationSender email = new Task04EmailNotificationSender("student@example.com");
        Task02NotificationSender console = new Task04ConsoleNotificationSender();
        System.out.println(email.send("Welcome"));
        System.out.println(console.send("Hello"));
        try {
            email.send(" ");
        } catch (IllegalArgumentException e) {
            System.out.println("validation: " + e.getMessage());
        }
    }

    private static void task5() {
        System.out.println("== 5. type choices ==");
        Task00Course course = new Task00Course("C-1", "Java");
        Task05Identifiable courseId = course;
        Task05Identifiable studentId = new Task00Student("S-1", 20);
        Task05BaseEntity instructor = new Task05Instructor("I-1", "Ada");
        Task05Formatter<Task00Course> formatter = new Task01CompactCourseFormatter();
        Clock clock = Clock.fixed(
                Instant.parse("2026-10-25T10:00:00Z"), ZoneOffset.UTC);
        Task05EnrollmentWindow window = new Task05EnrollmentWindow(clock);
        System.out.println("course id: " + courseId.id());
        System.out.println("student id: " + studentId.id());
        System.out.println("instructor: " + instructor.id());
        System.out.println("formatted: " + formatter.format(course));
        System.out.println("window open: " + window.isOpen(
                Instant.parse("2026-10-25T09:00:00Z"),
                Instant.parse("2026-10-25T11:00:00Z")));
    }

    private static void task6() {
        System.out.println("== 6. override vs overload ==");
        Task06MessageFormatter legacy = new Task06LegacyUpperFormatter();
        Task06LegacyUpperFormatter legacyConcrete = new Task06LegacyUpperFormatter();
        Task06MessageFormatter fixed = new Task06UpperMessageFormatter();
        System.out.println(legacy.format("hello"));
        System.out.println(legacyConcrete.format((Object) "hello"));
        System.out.println(fixed.format("hello"));
    }

    private static void task7() {
        System.out.println("== 7. decorators ==");
        Consumer<String> logger = line -> System.out.println("  " + line);
        Task02NotificationSender legacy = new Task07LoggingEmailSender(logger);
        System.out.println("legacy: " + legacy.send("legacy"));
        Task02NotificationSender loggingOutside = new Task07LoggingSender(
                new Task07RetrySender(new Task07FlakySender(new Task02EmailSender(), 2), 3), logger);
        System.out.println("order 1: " + loggingOutside.send("order-1"));
        Task02NotificationSender loggingInside = new Task07RetrySender(
                new Task07LoggingSender(new Task07FlakySender(new Task02EmailSender(), 2), logger), 3);
        System.out.println("order 2: " + loggingInside.send("order-2"));
        Task07MetricsSender metricsOuter = new Task07MetricsSender(
                new Task07RetrySender(new Task07FlakySender(new Task02EmailSender(), 2), 3));
        metricsOuter.send("metrics-outer");
        System.out.println("metrics outer: attempts=" + metricsOuter.attempts()
                + ", successes=" + metricsOuter.successes()
                + ", failures=" + metricsOuter.failures());
        Task07MetricsSender metricsInner = new Task07MetricsSender(
                new Task07FlakySender(new Task02EmailSender(), 2));
        new Task07RetrySender(metricsInner, 3).send("metrics-inner");
        System.out.println("metrics inner: attempts=" + metricsInner.attempts()
                + ", successes=" + metricsInner.successes()
                + ", failures=" + metricsInner.failures());
    }

    private static void task8() {
        System.out.println("== 8. read and write contracts ==");
        Task08InMemoryCourseRepository repository = new Task08InMemoryCourseRepository();
        repository.save(new Task00Course("C-1", "Java"));
        repository.save(new Task00Course("C-2", "SQL"));
        Task08CourseReader readOnly = new Task08ReadOnlyCourseRepository(repository.findAll());
        Task08CourseCatalogView view = new Task08CourseCatalogView(readOnly);
        System.out.println(view.render());
        System.out.println(readOnly.findById("C-1").orElseThrow().title());
        repository.deleteById("C-2");
        System.out.println(new Task08CourseCatalogView(repository).render());
    }

    private static void task9() {
        System.out.println("== 9. policy strategy ==");
        Task00Student student = new Task00Student("S-1", 17, Set.of("BASIC"), Set.of(), Set.of("MON-10"));
        Task00Course course = new Task00Course("C-2", "Advanced Java", 18, 1, Set.of("BASIC"), "TUE-10");
        Task00EnrollmentRequest request = new Task00EnrollmentRequest(student, course, 1);
        System.out.println(describe(new Task09EnrollmentService(new Task09AgePolicy()).enroll(request)));
        System.out.println(describe(new Task09EnrollmentService(new Task09PrerequisitePolicy()).enroll(request)));
        System.out.println(describe(new Task09EnrollmentService(new Task09CapacityPolicy()).enroll(request)));
        Task00Course tbd = new Task00Course("C-3", "Project", 0, 10, Set.of(), "TBD");
        Task00EnrollmentRequest review = new Task00EnrollmentRequest(student, tbd, 0);
        System.out.println(describe(new Task09EnrollmentService(new Task09ManualReviewPolicy()).enroll(review)));
    }

    private static void task10() {
        System.out.println("== 10. default conflict ==");
        System.out.println(new Task10AuditNotifier().describe());
    }

    private static void task11() {
        System.out.println("== 11. equality ==");
        Task11Point point = new Task11Point(1, 2);
        Task11ColoredPoint colored = new Task11ColoredPoint(1, 2, "red");
        System.out.println("point.equals(colored)=" + point.equals(colored));
        System.out.println("colored.equals(point)=" + colored.equals(point));
        Task11StrictPoint strictPoint = new Task11StrictPoint(1, 2);
        Task11StrictColoredPoint strictColored = new Task11StrictColoredPoint(1, 2, "red");
        System.out.println("strict point.equals(colored)=" + strictPoint.equals(strictColored));
        System.out.println("strict colored.equals(point)=" + strictColored.equals(strictPoint));
        Task11ComposedColoredPoint composed = new Task11ComposedColoredPoint(new Task11PointValue(1, 2), "red");
        Task11ComposedColoredPoint same = new Task11ComposedColoredPoint(new Task11PointValue(1, 2), "red");
        System.out.println("composed equals same=" + composed.equals(same));
        System.out.println("composed equals point=" + composed.equals(new Task11PointValue(1, 2)));
    }

    private static void task12() {
        System.out.println("== 12. sealed result ==");
        Task12EnrollmentResult[] results = {
                new Task12EnrollmentResult.Accepted("C-1"),
                new Task12EnrollmentResult.Rejected("age"),
                new Task12EnrollmentResult.WaitListed("C-2", 3),
                new Task12EnrollmentResult.UnderReview("C-3")
        };
        for (Task12EnrollmentResult result : results) {
            System.out.println(describe(result));
        }
    }

    private static void task13() {
        System.out.println("== 13. type code removed ==");
        Task13NotificationFactory factory = new Task13NotificationFactory();
        for (String type : List.of("email", "sms", "push")) {
            Task13Notification notification = factory.create(type);
            System.out.println(notification.render("CourseHub enrollment opened"));
        }
    }

    private static void task14() {
        System.out.println("== 14. composition ==");
        Task00Course course = new Task00Course("C-1", "Java");
        Task14CourseOffering[] offerings = {
                new Task14CourseOffering(course, new Task14OnlineFormat(), new Task14FreePayment(),
                        new BigDecimal("100.00")),
                new Task14CourseOffering(course, new Task14ClassroomFormat(), new Task14FixedPayment(),
                        new BigDecimal("200.00")),
                new Task14CourseOffering(course, new Task14HybridFormat(),
                        new Task14SubscriptionPayment(new BigDecimal("30.00")),
                        new BigDecimal("200.00")),
                new Task14CourseOffering(course, new Task14SelfPacedFormat(),
                        new Task14InstallmentPayment(new BigDecimal("10.00")),
                        new BigDecimal("150.00"))
        };
        for (Task14CourseOffering offering : offerings) {
            System.out.println(offering.describe());
        }
    }

    private static void task15() {
        System.out.println("== 15. rules engine ==");
        Task00Student student = new Task00Student(
                "S-2", 20, Set.of("BASIC"), Set.of(), Set.of("FRI-10"));
        Task00Course course = new Task00Course(
                "C-5", "Advanced Java", 18, 2, Set.of("BASIC"), "MON-10");
        Task00EnrollmentRequest request = new Task00EnrollmentRequest(student, course, 0);
        Task15EnrollmentRule[] rules = {
                new Task15AgeRule(),
                new Task15PrerequisitesRule(),
                new Task15CapacityRule(),
                new Task15ScheduleConflictRule(),
                new Task15DuplicateEnrollmentRule()
        };
        for (Task15EnrollmentRule rule : rules) {
            System.out.println(rule.getClass().getSimpleName()
                    + " -> " + describeRule(rule.check(request)));
        }
        Task15EnrollmentEngine engine = new Task15EnrollmentEngine(new Task15AllOfRule(rules));
        System.out.println("allOf accepted: " + describe(engine.enroll(request)));
        Task00EnrollmentRequest conflict = new Task00EnrollmentRequest(
                new Task00Student("S-3", 20, Set.of("BASIC"), Set.of(), Set.of("MON-10")),
                course, 0);
        System.out.println("allOf rejected: " + describe(engine.enroll(conflict)));
        Task00EnrollmentRequest young = new Task00EnrollmentRequest(
                new Task00Student("S-4", 16, Set.of("BASIC"), Set.of(), Set.of()),
                course, 0);
        Task15EnrollmentRule any = new Task15AnyOfRule(new Task15AgeRule(), new Task15PrerequisitesRule());
        System.out.println("anyOf: " + describeRule(any.check(young)));
        Task15EnrollmentEngine anyEngine = new Task15EnrollmentEngine(any);
        System.out.println("anyOf engine: " + describe(anyEngine.enroll(young)));
    }

    private static String describe(Task12EnrollmentResult result) {
        return switch (result) {
            case Task12EnrollmentResult.Accepted accepted ->
                    "ACCEPTED " + accepted.courseCode();
            case Task12EnrollmentResult.Rejected rejected ->
                    "REJECTED " + rejected.reason();
            case Task12EnrollmentResult.WaitListed waitListed ->
                    "WAITLISTED " + waitListed.courseCode() + " #" + waitListed.position();
            case Task12EnrollmentResult.UnderReview underReview ->
                    "UNDER REVIEW " + underReview.courseCode();
        };
    }

    private static String describeRule(Task15RuleResult result) {
        return switch (result) {
            case Task15RuleResult.Passed passed -> "pass";
            case Task15RuleResult.Violated violation ->
                    "violated " + violation.code() + ": " + violation.message();
        };
    }
}
