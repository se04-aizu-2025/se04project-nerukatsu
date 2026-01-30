package algorithm;

import java.util.function.BiConsumer;

public abstract class AbstractSortAlgorithm implements SortAlgorithm {
    private int[] data;
    protected BiConsumer<Integer, Integer> visualizer;

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
    
    /**
     * ビジュアライザーにコールバックを登録
     * @param visualizer 配列インデックスのペア (i, j) を受け取るコンシューマ
     */
    public void setVisualizerCallback(BiConsumer<Integer, Integer> visualizer) {
        this.visualizer = visualizer;
    }
    
    /**
     * ビジュアライザーに通知（アルゴリズムから呼び出す）
     */
    protected void notifyVisualizer(int i, int j) {
        if (visualizer != null) {
            visualizer.accept(i, j);
        }    }
}