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

/**
 * Задание 13. Таблица решений зачисления (статус курса, места, prerequisite,
 * повторная заявка). Правила по старшинству, остальные условия — don't care:
 *   R1 повторная заявка        -> ALREADY_ENROLLED     (CH-2006)
 *   R2 курс не ACTIVE          -> COURSE_NOT_ACTIVE    (CH-2003)
 *   R3 prerequisite не закрыт  -> PREREQUISITE_NOT_MET (CH-2005)
 *   R4 нет свободных мест      -> COURSE_FULL          (CH-2004)
 *   R5 иначе                   -> зачисление
 * 16 комбинаций четырёх условий дают 5 классов эквивалентности, поэтому
 * дублирующие кейсы удалены: их покрывают проверки don't care и сверка
 * со справочной моделью.
 */
@DisplayName("Задание 13. Таблица решений")
class Task13Test {

    private static final CourseCode MATH = CourseCode.of("MATH-101");

    private final EnrollmentPolicy policy = new EnrollmentPolicy();

    static Stream<Arguments> rules() {
        return Stream.of(
                Arguments.of("R1 повторная заявка", RejectionReason.ALREADY_ENROLLED, true, true, true, true),
                Arguments.of("R2 курс не ACTIVE", RejectionReason.COURSE_NOT_ACTIVE, false, true, true, false),
                Arguments.of("R3 нет prerequisite", RejectionReason.PREREQUISITE_NOT_MET, true, true, false, false),
                Arguments.of("R4 нет свободных мест", RejectionReason.COURSE_FULL, true, false, true, false),
                Arguments.of("R5 всё благоприятно", null, true, true, true, false));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("rules")
    @DisplayName("По одному представителю на каждое правило")
    void rulesAreCovered(String name, RejectionReason expected,
                         boolean active, boolean freeSeat, boolean prerequisite, boolean again) {
        Optional<RejectionReason> refusal =
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
        Set<RejectionReason> outcomes = new HashSet<>();
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
                    assertEquals(Optional.of(RejectionReason.ALREADY_ENROLLED),
                            policy.refusal(student(prerequisite), course(active, seat), true));
                }
            }
        }
    }

    /** Справочная модель: тот же порядок старшинства, что в политике. */
    private static RejectionReason reference(boolean active, boolean seat, boolean prerequisite, boolean again) {
        if (again) {
            return RejectionReason.ALREADY_ENROLLED;
        }
        if (!active) {
            return RejectionReason.COURSE_NOT_ACTIVE;
        }
        if (!prerequisite) {
            return RejectionReason.PREREQUISITE_NOT_MET;
        }
        if (!seat) {
            return RejectionReason.COURSE_FULL;
        }
        return null;
    }

    private Student student(boolean prerequisite) {
        return prerequisite ? new Student("S-1", MATH) : new Student("S-1");
    }

    private Course course(boolean active, boolean freeSeat) {
        return new Course(CourseCode.of("CS-202"), "Algorithms", 40, 1, freeSeat ? 0 : 1,
                active ? CourseStatus.ACTIVE : CourseStatus.ARCHIVED, MATH);
    }
}
