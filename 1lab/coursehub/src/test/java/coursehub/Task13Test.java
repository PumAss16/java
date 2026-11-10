package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Задание 13. Таблица решений")
class Task13Test {

    private static final Task10CourseCode MATH = Task10CourseCode.of("MATH-101");

    private final Task13EnrollmentPolicy policy = new Task13EnrollmentPolicy();

    static Stream<Arguments> rules() {
        return Stream.of(
                Arguments.of("R1 повторная заявка", Task03RejectionReason.ALREADY_ENROLLED, true, true, true, true),
                Arguments.of("R2 курс не ACTIVE", Task03RejectionReason.COURSE_NOT_ACTIVE, false, true, true, false),
                Arguments.of("R3 нет prerequisite", Task03RejectionReason.PREREQUISITE_NOT_MET, true, true, false, false),
                Arguments.of("R4 нет свободных мест", Task03RejectionReason.COURSE_FULL, true, false, true, false),
                Arguments.of("R5 всё благоприятно", null, true, true, true, false));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("rules")
    @DisplayName("По одному представителю на каждое правило")
    void rulesAreCovered(String name, Task03RejectionReason expected,
                         boolean active, boolean freeSeat, boolean prerequisite, boolean again) {
        Optional<Task03RejectionReason> refusal =
                policy.refusal(student(prerequisite), course(active, freeSeat), again);

        if (expected == null) {
            assertTrue(refusal.isEmpty(), name);
        } else {
            assertEquals(Optional.of(expected), refusal, name);
        }
    }

    @Test
    @DisplayName("16 комбинаций дают 5 исходов: 11 дубликатов покрытия не добавляют")
    void combinationsCollapseToFiveOutcomes() {
        Set<Task03RejectionReason> outcomes = new HashSet<>();
        for (boolean active : new boolean[]{true, false}) {
            for (boolean seat : new boolean[]{true, false}) {
                for (boolean prerequisite : new boolean[]{true, false}) {
                    for (boolean again : new boolean[]{true, false}) {
                        outcomes.add(reference(active, seat, prerequisite, again));
                    }
                }
            }
        }

        assertAll(
                () -> assertEquals(5, outcomes.size()),
                () -> assertTrue(outcomes.contains(null), "успешное зачисление — тоже класс"),
                () -> assertEquals(5, rules().count()));
    }

    @Test
    @DisplayName("Don't care: при повторной заявке остальные условия не влияют (8 комбинаций)")
    void alreadyEnrolledIgnoresOtherConditions() {
        for (boolean active : new boolean[]{true, false}) {
            for (boolean seat : new boolean[]{true, false}) {
                for (boolean prerequisite : new boolean[]{true, false}) {
                    assertEquals(Optional.of(Task03RejectionReason.ALREADY_ENROLLED),
                            policy.refusal(student(prerequisite), course(active, seat), true));
                }
            }
        }
    }

    private static Task03RejectionReason reference(boolean active, boolean seat, boolean prerequisite, boolean again) {
        if (again) {
            return Task03RejectionReason.ALREADY_ENROLLED;
        }
        if (!active) {
            return Task03RejectionReason.COURSE_NOT_ACTIVE;
        }
        if (!prerequisite) {
            return Task03RejectionReason.PREREQUISITE_NOT_MET;
        }
        if (!seat) {
            return Task03RejectionReason.COURSE_FULL;
        }
        return null;
    }

    private Task00Student student(boolean prerequisite) {
        return prerequisite ? new Task00Student("S-1", MATH) : new Task00Student("S-1");
    }

    private Task00Course course(boolean active, boolean freeSeat) {
        return new Task00Course(Task10CourseCode.of("CS-202"), "Algorithms", 40, 1, freeSeat ? 0 : 1,
                active ? Task13CourseStatus.ACTIVE : Task13CourseStatus.ARCHIVED, MATH);
    }
}
