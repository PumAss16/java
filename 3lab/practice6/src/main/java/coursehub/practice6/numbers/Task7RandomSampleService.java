package coursehub.practice6.numbers;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

class RandomSampleService {
    static double[] seeded(int size, int min, int max, long seed) {
        RandomGenerator generator = RandomGeneratorFactory.of("L64X128MixRandom").create(seed);
        return sample(generator, size, min, max);
    }

    static double[] production(int size, int min, int max) {
        return sample(RandomGenerator.getDefault(), size, min, max);
    }

    private static double[] sample(RandomGenerator generator, int size, int min, int max) {
        if (size <= 0 || min > max) {
            throw new IllegalArgumentException("invalid size or range");
        }
        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = generator.nextInt(min, max + 1);
        }
        return values;
    }
}
