package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Задание 3. Своё исключение: три атрибута, сохранённый cause, минимум данных в тексте. */
@DisplayName("Задание 3. EnrollmentRejectedException")
class Task03Test {

    @Test
    @DisplayName("studentId, courseId и reason code доступны программно")
    void hasAllAttributes() {
        EnrollmentRejectedException e =
                new EnrollmentRejectedException("S-42", "CS-101", RejectionReason.COURSE_FULL);

        assertAll(
                () -> assertEquals("S-42", e.studentId()),
                () -> assertEquals("CS-101", e.courseId()),
                () -> assertEquals(RejectionReason.COURSE_FULL, e.reason()),
                () -> assertEquals("CH-2004", e.code()));
    }

    @Test
    @DisplayName("Исходная причина сохраняется при оборачивании")
    void keepsCause() {
        IOException io = new IOException("disk is read-only");

        EnrollmentRejectedException e =
                new EnrollmentRejectedException("S-42", "CS-101", RejectionReason.COURSE_NOT_FOUND, io);

        assertAll(
                () -> assertSame(io, e.getCause()),
                () -> assertTrue(e.getMessage().contains("COURSE_NOT_FOUND")));
    }

    @Test
    @DisplayName("message и toString не раскрывают studentId")
    void doesNotLeakStudentId() {
        EnrollmentRejectedException e =
                new EnrollmentRejectedException("S-42", "CS-101", RejectionReason.ALREADY_ENROLLED);

        assertAll(
                () -> assertFalse(e.getMessage().contains("S-42")),
                () -> assertFalse(e.toString().contains("S-42")),
                () -> assertTrue(e.getMessage().contains("CS-101")),
                () -> assertTrue(e.getMessage().contains("ALREADY_ENROLLED")));
    }

    @Test
    @DisplayName("Каждая причина отказа даёт стабильный код CH-2001..CH-2006")
    void reasonsHaveStableCodes() {
        for (RejectionReason reason : RejectionReason.values()) {
            EnrollmentRejectedException e = new EnrollmentRejectedException("S-42", "CS-101", reason);
            assertTrue(e.code().matches("CH-2\\d{3}"), reason.name());
        }
    }
}
