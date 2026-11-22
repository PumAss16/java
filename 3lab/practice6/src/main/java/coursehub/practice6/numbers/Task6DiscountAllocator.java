package coursehub.practice6.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

class DiscountAllocator {
    static List<BigDecimal> allocate(BigDecimal total, List<BigDecimal> weights, RoundingMode mode) {
        if (total == null || total.signum() < 0) {
            throw new IllegalArgumentException("total must be non-negative");
        }
        if (weights == null || weights.isEmpty()) {
            throw new IllegalArgumentException("weights required");
        }
        BigDecimal weightSum = BigDecimal.ZERO;
        for (BigDecimal weight : weights) {
            if (weight == null || weight.signum() < 0) {
                throw new IllegalArgumentException("weights must be non-negative");
            }
            weightSum = weightSum.add(weight);
        }
        if (weightSum.signum() == 0) {
            throw new IllegalArgumentException("weights sum must be positive");
        }
        long kopecks = total.movePointRight(2).longValueExact();
        int size = weights.size();
        long[] parts = new long[size];
        BigDecimal[] fractions = new BigDecimal[size];
        long allocated = 0;
        for (int i = 0; i < size; i++) {
            BigDecimal exact = BigDecimal.valueOf(kopecks).multiply(weights.get(i));
            parts[i] = exact.divide(weightSum, 0, mode).longValueExact();
            BigDecimal raw = exact.divide(weightSum, 20, RoundingMode.FLOOR);
            fractions[i] = raw.subtract(raw.setScale(0, RoundingMode.FLOOR));
            allocated += parts[i];
        }
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            order.add(i);
        }
        order.sort((first, second) -> {
            int byFraction = fractions[second].compareTo(fractions[first]);
            return byFraction != 0 ? byFraction : Integer.compare(first, second);
        });
        long rest = kopecks - allocated;
        long step = rest > 0 ? 1 : -1;
        for (int i = 0; i < Math.abs(rest); i++) {
            parts[order.get(i)] += step;
        }
        List<BigDecimal> result = new ArrayList<>();
        for (long part : parts) {
            result.add(BigDecimal.valueOf(part).movePointLeft(2));
        }
        return result;
    }
}
