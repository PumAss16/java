package coursehub.practice3.equality;

import java.util.Objects;

public final class Task11StrictColoredPoint extends Task11StrictPoint {

    private final String color;

    public Task11StrictColoredPoint(int x, int y, String color) {
        super(x, y);
        this.color = Objects.requireNonNull(color, "color");
    }

    public String color() {
        return color;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Task11StrictColoredPoint point = (Task11StrictColoredPoint) other;
        return color.equals(point.color);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + color.hashCode();
    }
}
