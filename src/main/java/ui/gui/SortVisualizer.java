package ui.gui;

import algorithm.SortAlgorithm;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ソートアルゴリズムの実行過程を可視化するパネル
 */
public class SortVisualizer extends JPanel {
    private int[] array;
    private int[] originalArray;
    private volatile int currentIndex = -1;
    private volatile int compareIndex = -1;
    private volatile boolean isSorting = false;
    private volatile boolean isSorted = false;
    private SortAlgorithm sortAlgorithm;
    private volatile int animationDelay = 5; // ミリ秒
    
    private static final Color UNSORTED_COLOR = new Color(100, 150, 255);
    private static final Color CURRENT_COLOR = new Color(255, 100, 100);
    private static final Color COMPARE_COLOR = new Color(255, 200, 0);
    private static final Color SORTED_COLOR = new Color(100, 255, 100);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 250);
    
    private long startTime = 0;
    private long endTime = 0;
    
    public SortVisualizer() {
        setPreferredSize(new Dimension(800, 400));
        setBackground(BACKGROUND_COLOR);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetVisualization();
            }
        });
    }
    
    /**
     * ビジュアライザーにデータを設定
     */
    public void setData(int[] data, SortAlgorithm algorithm) {
        this.array = data.clone();
        this.originalArray = data.clone();
        this.sortAlgorithm = algorithm;
        this.currentIndex = -1;
        this.compareIndex = -1;
        this.isSorted = false;
        this.isSorting = false;
        repaint();
    }
    
    /**
     * ソート処理を開始（別スレッドで実行）
     */
    public void startSorting() {
        if (array == null || isSorting) {
            return;
        }
        
        isSorting = true;
        isSorted = false;
        startTime = System.currentTimeMillis();
        
        Thread sortThread = new Thread(() -> {
            try {
                // アルゴリズムにビジュアライザーコールバックを登録
                if (sortAlgorithm instanceof algorithm.AbstractSortAlgorithm) {
                    algorithm.AbstractSortAlgorithm abstractAlgo = 
                        (algorithm.AbstractSortAlgorithm) sortAlgorithm;
                    abstractAlgo.setVisualizerCallback(this::updateArrayState);
                }
                
                // ソート実行
                sortAlgorithm.sort(array);
                
                endTime = System.currentTimeMillis();
                isSorted = true;
                currentIndex = -1;
                compareIndex = -1;
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isSorting = false;
            }
        });
        
        sortThread.setDaemon(true);
        sortThread.start();
    }
    
    /**
     * ソート中に配列の状態を更新（アルゴリズムから呼び出される）
     */
    protected void updateArrayState(int index1, int index2) {
        this.currentIndex = index1;
        this.compareIndex = index2;
        repaint();
        
        // アニメーション用の遅延
        try {
            Thread.sleep(animationDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * アニメーション速度を設定（ミリ秒単位）
     * @param delayMs 各ステップの遅延時間
     */
    public void setAnimationDelay(int delayMs) {
        this.animationDelay = Math.max(0, delayMs);
    }
    
    /**
     * 現在のアニメーション速度を取得
     */
    public int getAnimationDelay() {
        return animationDelay;
    }
    
    /**
     * 要素の交換を反映（アルゴリズムから呼び出される）
     */
    protected void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        updateArrayState(i, j);
    }
    
    /**
     * ビジュアライザーをリセット
     */
    public void resetVisualization() {
        if (!isSorting) {
            array = originalArray.clone();
            currentIndex = -1;
            compareIndex = -1;
            isSorted = false;
            startTime = 0;
            endTime = 0;
            repaint();
        }
    }
    
    /**
     * 現在ソート中かどうかを判定
     */
    public synchronized boolean isSorting() {
        return isSorting;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (array == null || array.length == 0) {
            drawEmptyMessage(g);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int arrayLength = array.length;
        
        // 各要素の幅と最大値を計算
        double barWidth = (double) (width - 40) / arrayLength;
        int maxValue = getMaxValue();
        
        // 情報パネルを描画
        drawInfoPanel(g2d, width, height);
        
        // 配列要素をバー図で描画
        int startX = 20;
        int startY = 60;
        int drawHeight = height - 100;
        
        for (int i = 0; i < arrayLength; i++) {
            double x = startX + i * barWidth;
            double barHeight = (double) array[i] / maxValue * drawHeight;
            
            // 色を決定
            Color barColor = UNSORTED_COLOR;
            if (isSorted) {
                barColor = SORTED_COLOR;
            } else if (i == currentIndex) {
                barColor = CURRENT_COLOR;
            } else if (i == compareIndex) {
                barColor = COMPARE_COLOR;
            }
            
            // バーを描画
            g2d.setColor(barColor);
            g2d.fillRect((int) x, (int) (startY + drawHeight - barHeight), 
                        (int) Math.max(1, barWidth - 1), (int) barHeight);
            
            // 枠線を描画
            g2d.setColor(new Color(80, 80, 80));
            g2d.setStroke(new BasicStroke(0.5f));
            g2d.drawRect((int) x, (int) (startY + drawHeight - barHeight), 
                        (int) Math.max(1, barWidth - 1), (int) barHeight);
        }
    }
    
    /**
     * 情報パネルを描画
     */
    private void drawInfoPanel(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(50, 50, 50));
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        String statusText = isSorting ? "ソート中..." : (isSorted ? "完了" : "準備完了");
        String arrayInfo = "配列サイズ: " + array.length;
        String timeInfo = "";
        
        if (isSorted && endTime > startTime) {
            long elapsedTime = endTime - startTime;
            timeInfo = "実行時間: " + elapsedTime + "ms";
        }
        
        g2d.drawString(statusText, 20, 30);
        g2d.drawString(arrayInfo, 200, 30);
        if (!timeInfo.isEmpty()) {
            g2d.drawString(timeInfo, 400, 30);
        }
    }
    
    /**
     * 空の状態のメッセージを描画
     */
    private void drawEmptyMessage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Serif", Font.ITALIC, 16));
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("データを生成して、ソートボタンをクリックしてください", 
                       getWidth() / 2 - 150, getHeight() / 2);
    }
    
    /**
     * 配列の最大値を取得
     */
    private int getMaxValue() {
        int max = Integer.MIN_VALUE;
        for (int val : array) {
            if (val > max) {
                max = val;
            }
        }
        return Math.max(max, 1);
    }
}
