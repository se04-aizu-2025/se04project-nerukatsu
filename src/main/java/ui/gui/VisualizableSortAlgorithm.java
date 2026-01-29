package ui.gui;

import algorithm.SortAlgorithm;

/**
 * ビジュアライザーでアニメーション対応のソートアルゴリズムラッパー
 */
public abstract class VisualizableSortAlgorithm implements SortAlgorithm {
    protected SortVisualizer visualizer;
    protected SortAlgorithm wrappedAlgorithm;
    
    public VisualizableSortAlgorithm(SortAlgorithm algorithm) {
        this.wrappedAlgorithm = algorithm;
    }
    
    public void setVisualizer(SortVisualizer visualizer) {
        this.visualizer = visualizer;
    }
    
    /**
     * ビジュアライザーに配列の状態を通知
     */
    protected void notifyVisualizerUpdate(int index1, int index2) {
        if (visualizer != null) {
            visualizer.updateArrayState(index1, index2);
        }
    }
    
    /**
     * ビジュアライザーに交換を通知
     */
    protected void notifyVisualizerSwap(int i, int j) {
        if (visualizer != null) {
            visualizer.swap(i, j);
        }
    }
    
    @Override
    public String getName() {
        return wrappedAlgorithm.getName();
    }
}
