package algorithm;

public class ShakerSort extends AbstractSortAlgorithm {

    @Override
    public void sort(int[] array) {
        int left = 0;
        int right = array.length - 1;
        boolean swapped;

        while (left < right) {
            swapped = false;
            // 左 → 右
            for (int i = left; i < right; i++) {
                if (array[i] > array[i + 1]) {
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                    swapped = true;
                }
            }
            right--;
            if (!swapped) break;
            swapped = false;
            // 右 → 左
            for (int i = right; i > left; i--) {
                if (array[i] < array[i - 1]) {
                    int temp = array[i];
                    array[i] = array[i - 1];
                    array[i - 1] = temp;
                    swapped = true;
                }
            }
            left++;
            if (!swapped) break;
        }
    }

    @Override
    public String getName() {
        return "Shaker Sort";
    }
}
