package algorithm;

public class ShakerSort extends AbstractSortAlgorithm {

    @Override
    public void sort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        shakerSort(array);
    }

    @Override
    public String getName() {
        return "Shaker Sort";
    }

    private void shakerSort(int[] a) {
        int left = 0;
        int right = a.length - 1;
        boolean swapped;

        while (left < right) {
            swapped = false;
            // 左 → 右
            for (int i = left; i < right; i++) {
                if (a[i] > a[i + 1]) {
                    int temp = a[i];
                    a[i] = a[i + 1];
                    a[i + 1] = temp;
                    swapped = true;
                }
            }
            right--;
            if (!swapped) break;
            swapped = false;
            // 右 → 左
            for (int i = right; i > left; i--) {
                if (a[i] < a[i - 1]) {
                    int temp = a[i];
                    a[i] = a[i - 1];
                    a[i - 1] = temp;
                    swapped = true;
                }
            }
            left++;
            if (!swapped) break;
        }
    }
}
