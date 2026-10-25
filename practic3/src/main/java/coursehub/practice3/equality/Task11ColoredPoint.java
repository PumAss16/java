package coursehub.practice3.equality;

import java.util.Objects;

public class Task11ColoredPoint extends Task11Point {

    private final String color;

    public Task11ColoredPoint(int x, int y, String color) {
        super(x, y);
        this.color = Objects.requireNonNull(color, "color");
    }

    public String color() {
        return color;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Task11ColoredPoint point)) {
            return false;
        }
        return super.equals(point) && color.equals(point.color);
    }
}
