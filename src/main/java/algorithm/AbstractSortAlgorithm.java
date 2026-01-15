package algorithm;

public abstract class AbstractSortAlgorithm implements SortAlgorithm {
    private int[] data;

    public void setData(int[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }
        this.data = data;
    }

    public int[] getData() {
        return data;
    }

    public void sort() {
        if (data == null) {
            throw new IllegalStateException("data is not set");
        }
        sort(data);
    }
}
