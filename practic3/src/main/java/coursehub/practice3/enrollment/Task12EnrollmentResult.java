package coursehub.practice3.enrollment;

public sealed interface Task12EnrollmentResult
        permits Task12EnrollmentResult.Accepted,
                Task12EnrollmentResult.Rejected,
                Task12EnrollmentResult.WaitListed,
                Task12EnrollmentResult.UnderReview {

    record Accepted(String courseCode) implements Task12EnrollmentResult {

        public Accepted {
            if (courseCode == null || courseCode.isBlank()) {
                throw new IllegalArgumentException("courseCode must not be blank");
            }
        }
    }

    record Rejected(String reason) implements Task12EnrollmentResult {

        public Rejected {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("reason must not be blank");
            }
        }
    }

    record WaitListed(String courseCode, int position) implements Task12EnrollmentResult {

        public WaitListed {
            if (courseCode == null || courseCode.isBlank()) {
                throw new IllegalArgumentException("courseCode must not be blank");
            }
            if (position < 1) {
                throw new IllegalArgumentException("position must be >= 1");
            }
        }
    }

    record UnderReview(String courseCode) implements Task12EnrollmentResult {

        public UnderReview {
            if (courseCode == null || courseCode.isBlank()) {
                throw new IllegalArgumentException("courseCode must not be blank");
            }
        }
    }
}
