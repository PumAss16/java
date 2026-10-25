package coursehub.practice3.equality;

import java.util.Objects;

public record Task11ComposedColoredPoint(Task11PointValue point, String color) {

    public Task11ComposedColoredPoint {
        Objects.requireNonNull(point, "point");
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("color must not be blank");
        }
    }
}
