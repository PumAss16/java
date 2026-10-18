package coursehub.practice6.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

record SampleStats(int count, double mean, double min, double max, double stdDev) {
    static SampleStats of(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values required");
        }
        double sum = 0;
        double min = values[0];
        double max = values[0];
        for (double value : values) {
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        double mean = sum / values.length;
        double squares = 0;
        for (double value : values) {
            squares += (value - mean) * (value - mean);
        }
        double stdDev = Math.sqrt(squares / values.length);
        return new SampleStats(values.length, mean, min, max, stdDev);
    }

    double meanRounded(int scale) {
        return BigDecimal.valueOf(mean).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}
