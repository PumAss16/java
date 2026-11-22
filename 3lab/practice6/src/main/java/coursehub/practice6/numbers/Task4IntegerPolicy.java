package coursehub.practice6.numbers;

import java.util.Objects;

class IntegerPolicy {
    static int unbox(Integer value) {
        return value;
    }

    static boolean sameReference(Integer left, Integer right) {
        return left == right;
    }

    static boolean sameValue(Integer left, Integer right) {
        return Objects.equals(left, right);
    }

    static int add(Integer left, Integer right) {
        if (left == null) {
            throw new IllegalArgumentException("left is null");
        }
        if (right == null) {
            throw new IllegalArgumentException("right is null");
        }
        return left + right;
    }
}
