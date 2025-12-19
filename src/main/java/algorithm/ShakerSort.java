public class ShakerSort {

    public static void shakerSort(int[] a) {
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
    public static void main(String[] args) {
        int[] data = {5, 3, 8, 4, 2};
        shakerSort(data);
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i] + " ");
        }
    }
}
