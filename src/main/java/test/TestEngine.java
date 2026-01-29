package test;

import java.util.Arrays;

public class TestEngine {

    /**
     * 配列が昇順に並んでいるかチェック
     * @param array チェック対象の配列
     * @return 昇順なら true、そうでなければ false
     */
    public static boolean isSorted(int[] array) {
        if (array == null || array.length <= 1) {
            return true;
        }
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * ソートが正しく実行されたかを完全に検証
     * 1. 昇順に並んでいるか
     * 2. 要素数が変わっていないか
     * 3. 中身（多重集合）が変わっていないか
     * 
     * @param original ソート前の配列
     * @param sorted ソート後の配列
     * @return 正しくソートされていたら true
     */
    public static boolean isValidSort(int[] original, int[] sorted) {
        // null チェック
        if (original == null || sorted == null) {
            return false;
        }

        // 要素数チェック
        if (original.length != sorted.length) {
            return false;
        }

        // 空配列は有効
        if (original.length == 0) {
            return true;
        }

        // 昇順チェック
        if (!isSorted(sorted)) {
            return false;
        }

        // 中身（多重集合）が同じかチェック
        // ソート後の配列とソート前の配列の要素を比較
        int[] originalCopy = Arrays.copyOf(original, original.length);
        int[] sortedCopy = Arrays.copyOf(sorted, sorted.length);
        
        Arrays.sort(originalCopy);
        Arrays.sort(sortedCopy);
        
        // ソート済みの2つの配列を比較
        return Arrays.equals(originalCopy, sortedCopy);
    }

    /**
     * テスト結果をレポート形式で返す
     * @param original ソート前の配列
     * @param sorted ソート後の配列
     * @return テスト結果の詳細
     */
    public static String generateTestReport(int[] original, int[] sorted) {
        StringBuilder report = new StringBuilder();
        
        report.append("=== Sort Validation Report ===\n");
        report.append(String.format("Original array length: %d\n", 
            original != null ? original.length : -1));
        report.append(String.format("Sorted array length: %d\n", 
            sorted != null ? sorted.length : -1));
        
        // null チェック
        if (original == null || sorted == null) {
            report.append("❌ FAIL: Array is null\n");
            return report.toString();
        }

        // 要素数チェック
        boolean lengthOk = original.length == sorted.length;
        report.append(String.format("✓ Length preserved: %s\n", 
            lengthOk ? "YES" : "NO"));

        // 昇順チェック
        boolean sortedOk = isSorted(sorted);
        report.append(String.format("✓ Array is sorted: %s\n", 
            sortedOk ? "YES" : "NO"));

        // 要素保存チェック
        if (original.length > 0) {
            int[] originalCopy = Arrays.copyOf(original, original.length);
            int[] sortedCopy = Arrays.copyOf(sorted, sorted.length);
            Arrays.sort(originalCopy);
            Arrays.sort(sortedCopy);
            boolean elementsOk = Arrays.equals(originalCopy, sortedCopy);
            report.append(String.format("✓ Elements preserved: %s\n", 
                elementsOk ? "YES" : "NO"));
        }

        // 最終結果
        boolean valid = isValidSort(original, sorted);
        report.append(String.format("\n%s OVERALL: %s\n", 
            valid ? "✅" : "❌", valid ? "PASS" : "FAIL"));
        
        return report.toString();    }
}