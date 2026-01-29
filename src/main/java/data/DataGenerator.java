package data;

import java.util.Random;

/**
 * ソートアルゴリズムのテスト用配列を生成するクラス
 */
public class DataGenerator {
    private static final int DEFAULT_MAX = 100;
    private final Random random;

    /**
     * 配列の種類を表すEnum
     */
    public enum ArrayType {
        RANDOM("Random"),
        REVERSED("Reversed"),
        NEARLY_SORTED("Nearly Sorted");

        private final String displayName;

        ArrayType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public DataGenerator() {
        this(new Random());
    }

    public DataGenerator(long seed) {
        this(new Random(seed));
    }

    private DataGenerator(Random random) {
        this.random = random;
    }

    /**
     * ランダム配列を生成
     */
    public int[] generateRandomArray(int size) {
        return generateRandomArray(size, 0, DEFAULT_MAX);
    }

    /**
     * ランダム配列を生成
     */
    public int[] generateRandomArray(int size, int maxInclusive) {
        return generateRandomArray(size, 0, maxInclusive);
    }

    /**
     * ランダム配列を生成
     */
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

    /**
     * 指定された種類の配列を生成
     * @param type 配列の種類（RANDOM, REVERSED, NEARLY_SORTED）
     * @param size 配列のサイズ
     * @return 生成された配列
     */
    public int[] generateArray(ArrayType type, int size) {
        return generateArray(type, size, 0, DEFAULT_MAX);
    }

    /**
     * 指定された種類の配列を生成
     * @param type 配列の種類
     * @param size 配列のサイズ
     * @param minInclusive 最小値
     * @param maxInclusive 最大値
     * @return 生成された配列
     */
    public int[] generateArray(ArrayType type, int size, int minInclusive, int maxInclusive) {
        switch (type) {
            case RANDOM:
                return generateRandomArray(size, minInclusive, maxInclusive);
            case REVERSED:
                return generateReversedArray(size, minInclusive, maxInclusive);
            case NEARLY_SORTED:
                return generateNearlySortedArray(size, minInclusive, maxInclusive);
            default:
                throw new IllegalArgumentException("Unknown array type: " + type);
        }
    }

    /**
     * 逆順に並んだ配列を生成
     * @param size 配列のサイズ
     * @param minInclusive 最小値
     * @param maxInclusive 最大値
     * @return 降順に並んだ配列
     */
    private int[] generateReversedArray(int size, int minInclusive, int maxInclusive) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0");
        }
        if (minInclusive > maxInclusive) {
            throw new IllegalArgumentException("minInclusive must be <= maxInclusive");
        }

        int[] data = new int[size];
        int range = maxInclusive - minInclusive + 1;
        
        // 昇順に値を割り当てて逆順に配置
        for (int i = 0; i < size; i++) {
            data[i] = maxInclusive - (i % range);
        }
        
        return data;
    }

    /**
     * ほぼ整列された配列を生成
     * ランダムな配列から90%をソートして、10%だけシャッフルさせる
     * @param size 配列のサイズ
     * @param minInclusive 最小値
     * @param maxInclusive 最大値
     * @return ほぼ整列された配列
     */
    private int[] generateNearlySortedArray(int size, int minInclusive, int maxInclusive) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0");
        }
        if (minInclusive > maxInclusive) {
            throw new IllegalArgumentException("minInclusive must be <= maxInclusive");
        }

        // 最初にランダム配列を生成してソート
        int[] data = generateRandomArray(size, minInclusive, maxInclusive);
        java.util.Arrays.sort(data);

        // 全体の10%の要素をランダムに入れ替え
        int swapCount = Math.max(1, size / 10);
        for (int i = 0; i < swapCount; i++) {
            int idx1 = random.nextInt(size);
            int idx2 = random.nextInt(size);
            
            // スワップ
            int temp = data[idx1];
            data[idx1] = data[idx2];
            data[idx2] = temp;
        }

        return data;
    }

    /**
     * 利用可能な配列タイプの一覧を取得
     */
    public static ArrayType[] getAvailableTypes() {
        return ArrayType.values();    }
}