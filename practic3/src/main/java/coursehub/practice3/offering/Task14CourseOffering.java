package coursehub.practice3.offering;

import coursehub.practice3.domain.Task00Course;
import java.math.BigDecimal;
import java.util.Objects;

public record Task14CourseOffering(
        Task00Course course,
        Task14CourseFormat format,
        Task14PaymentPolicy payment,
        BigDecimal basePrice) {

    public Task14CourseOffering {
        Objects.requireNonNull(course, "course");
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(payment, "payment");
        Objects.requireNonNull(basePrice, "basePrice");
        if (basePrice.signum() < 0) {
            throw new IllegalArgumentException("basePrice must be >= 0");
        }
    }

    public BigDecimal total() {
        return payment.total(basePrice);
    }

    public String describe() {
        return course.code()
                + " | " + format.name() + " (" + format.place() + ")"
                + " | " + payment.name()
                + " | " + total();
    }
}
