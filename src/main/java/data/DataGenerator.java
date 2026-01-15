package data;

import java.util.Random;

public class DataGenerator {
    private static final int DEFAULT_MAX = 100;
    private final Random random;

    public DataGenerator() {
        this(new Random());
    }

    public DataGenerator(long seed) {
        this(new Random(seed));
    }

    private DataGenerator(Random random) {
        this.random = random;
    }

    public int[] generateRandomArray(int size) {
        return generateRandomArray(size, 0, DEFAULT_MAX);
    }

    public int[] generateRandomArray(int size, int maxInclusive) {
        return generateRandomArray(size, 0, maxInclusive);
    }

    public int[] generateRandomArray(int size, int minInclusive, int maxInclusive) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0");
        }
        if (minInclusive > maxInclusive) {
            throw new IllegalArgumentException("minInclusive must be <= maxInclusive");
        }

        int[] data = new int[size];
        int bound = maxInclusive - minInclusive + 1;
        for (int i = 0; i < size; i++) {
            data[i] = minInclusive + random.nextInt(bound);
        }
        return data;
    }
}
