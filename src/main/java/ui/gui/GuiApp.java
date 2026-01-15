package ui.gui;

import algorithm.BubbleSort;
import algorithm.QuickSort;
import algorithm.ShakerSort;
import algorithm.SortAlgorithm;
import data.DataGenerator;
import test.TestEngine;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiApp {
    private static final int MAX_DISPLAY = 1000;
    private static final int PER_LINE = 14;
    private static final int MAX_VISUAL_SIZE = 200;
    private static final int DEFAULT_DELAY_MS = 40;

    private final DataGenerator dataGenerator = new DataGenerator();
    private final SortAlgorithm[] algorithms = {
        new BubbleSort(),
        new QuickSort(),
        new ShakerSort()
    };

    private JFrame frame;
    private JComboBox<String> algorithmCombo;
    private JSpinner sizeSpinner;
    private JSpinner minSpinner;
    private JSpinner maxSpinner;
    private JSpinner delaySpinner;
    private JTextArea unsortedArea;
    private JTextArea sortedArea;
    private JLabel statusLabel;
    private JLabel metricsLabel;
    private JButton generateButton;
    private JButton sortButton;
    private JButton pauseButton;
    private VisualizerPanel visualizerPanel;

    private int[] currentArray;
    private int[] visualArray;

    private SortPlan activePlan;
    private List<SortStep> steps;
    private int stepIndex;
    private int comparisons;
    private int swaps;

    private Timer animationTimer;
    private String runningStatus = "Ready.";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiApp().createAndShowGui());
    }

    private void createAndShowGui() {
        frame = new JFrame("Sorting Lab");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(new Color(246, 242, 236));
        frame.setContentPane(content);

        content.add(buildHeader(), BorderLayout.NORTH);
        content.add(buildMainPanel(), BorderLayout.CENTER);
        content.add(buildStatusBar(), BorderLayout.SOUTH);

        frame.setMinimumSize(new Dimension(940, 680));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        generateData();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(40, 46, 60));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Sorting Lab");
        title.setForeground(new Color(245, 245, 245));
        title.setFont(new Font("Georgia", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Watch elements move while the algorithm sorts.");
        subtitle.setForeground(new Color(202, 210, 220));
        subtitle.setFont(new Font("Serif", Font.PLAIN, 13));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setBackground(new Color(246, 242, 236));
        main.add(buildControlsPanel(), BorderLayout.WEST);
        main.add(buildDataPanel(), BorderLayout.CENTER);
        return main;
    }

    private JPanel buildControlsPanel() {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(new Color(251, 249, 245));
        controls.setBorder(BorderFactory.createTitledBorder("Controls"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        algorithmCombo = new JComboBox<>();
        for (SortAlgorithm algorithm : algorithms) {
            algorithmCombo.addItem(algorithm.getName());
        }

        sizeSpinner = new JSpinner(new SpinnerNumberModel(40, 0, 100000, 1));
        minSpinner = new JSpinner(new SpinnerNumberModel(0, -100000, 100000, 1));
        maxSpinner = new JSpinner(new SpinnerNumberModel(100, -100000, 100000, 1));
        delaySpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_DELAY_MS, 1, 1000, 5));
        delaySpinner.addChangeListener(event -> updateTimerDelay());

        generateButton = new JButton("Generate");
        sortButton = new JButton("Visualize");
        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);

        generateButton.addActionListener(event -> generateData());
        sortButton.addActionListener(event -> startSort());
        pauseButton.addActionListener(event -> togglePause());

        int row = 0;
        addControlRow(controls, gbc, row++, "Algorithm", algorithmCombo);
        addControlRow(controls, gbc, row++, "Array size", sizeSpinner);
        addControlRow(controls, gbc, row++, "Minimum", minSpinner);
        addControlRow(controls, gbc, row++, "Maximum", maxSpinner);
        addControlRow(controls, gbc, row++, "Delay (ms)", delaySpinner);

        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controls.add(generateButton, gbc);

        gbc.gridy = row++;
        controls.add(sortButton, gbc);

        gbc.gridy = row;
        controls.add(pauseButton, gbc);

        return controls;
    }

    private void addControlRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JSpinner spinner) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(spinner, gbc);
    }

    private void addControlRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComboBox<String> comboBox) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(comboBox, gbc);
    }

    private JPanel buildDataPanel() {
        visualizerPanel = new VisualizerPanel();
        JPanel visualWrapper = new JPanel(new BorderLayout());
        visualWrapper.setBackground(new Color(246, 242, 236));
        visualWrapper.setBorder(BorderFactory.createTitledBorder("Visualizer"));
        visualWrapper.add(visualizerPanel, BorderLayout.CENTER);

        unsortedArea = createDataArea();
        sortedArea = createDataArea();

        JPanel leftPanel = wrapArea("Unsorted", unsortedArea);
        JPanel rightPanel = wrapArea("Sorted", sortedArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);
        splitPane.setBorder(null);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(246, 242, 236));
        bottomPanel.add(splitPane, BorderLayout.CENTER);

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, visualWrapper, bottomPanel);
        verticalSplit.setResizeWeight(0.6);
        verticalSplit.setDividerLocation(0.6);
        verticalSplit.setBorder(null);

        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBackground(new Color(246, 242, 236));
        dataPanel.add(verticalSplit, BorderLayout.CENTER);
        return dataPanel;
    }

    private JTextArea createDataArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(255, 255, 255));
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
        return area;
    }

    private JPanel wrapArea(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(246, 242, 236));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(8, 4, 4, 4));
        statusLabel = new JLabel(runningStatus);
        metricsLabel = new JLabel("Comparisons: 0 | Swaps: 0");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(metricsLabel, BorderLayout.EAST);
        return statusPanel;
    }

    private void generateData() {
        stopAnimation();

        int size = (Integer) sizeSpinner.getValue();
        int min = (Integer) minSpinner.getValue();
        int max = (Integer) maxSpinner.getValue();

        if (min > max) {
            JOptionPane.showMessageDialog(frame, "Minimum must be <= maximum.", "Invalid range",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentArray = dataGenerator.generateRandomArray(size, min, max);
        unsortedArea.setText(formatArray(currentArray));
        sortedArea.setText("");
        visualizerPanel.setData(currentArray);
        visualizerPanel.clearHighlights();
        steps = null;
        comparisons = 0;
        swaps = 0;
        stepIndex = 0;
        updateMetricsLabel();
        setStatus(String.format("Generated %,d values in [%d, %d].", size, min, max));
    }

    private void startSort() {
        if (currentArray == null) {
            JOptionPane.showMessageDialog(frame, "Generate an array first.", "No data",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (currentArray.length > MAX_VISUAL_SIZE) {
            int choice = JOptionPane.showConfirmDialog(frame,
                "Visualization is limited to " + MAX_VISUAL_SIZE + " items.\n" +
                    "Reduce the size to " + MAX_VISUAL_SIZE + " and continue?",
                "Array too large",
                JOptionPane.OK_CANCEL_OPTION);
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }
            sizeSpinner.setValue(MAX_VISUAL_SIZE);
            generateData();
        }

        stopAnimation();
        setControlsEnabled(false);
        pauseButton.setEnabled(false);
        setStatus("Building visualization steps...");
        metricsLabel.setText("Preparing steps...");
        sortedArea.setText("");

        int algorithmIndex = algorithmCombo.getSelectedIndex();
        int[] workingCopy = Arrays.copyOf(currentArray, currentArray.length);

        SwingWorker<SortPlan, Void> worker = new SwingWorker<SortPlan, Void>() {
            @Override
            protected SortPlan doInBackground() {
                return buildSortPlan(algorithmIndex, workingCopy);
            }

            @Override
            protected void done() {
                try {
                    SortPlan plan = get();
                    activePlan = plan;
                    steps = plan.steps;
                    stepIndex = 0;
                    comparisons = 0;
                    swaps = 0;
                    visualArray = Arrays.copyOf(currentArray, currentArray.length);
                    visualizerPanel.setData(visualArray);
                    visualizerPanel.clearHighlights();
                    updateMetricsLabel();
                    pauseButton.setEnabled(true);
                    pauseButton.setText("Pause");
                    startAnimation();
                } catch (Exception ex) {
                    setControlsEnabled(true);
                    pauseButton.setEnabled(false);
                    metricsLabel.setText("Comparisons: 0 | Swaps: 0");
                    setStatus("Visualization failed.");
                    JOptionPane.showMessageDialog(frame, "Visualization failed: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void startAnimation() {
        int delay = (Integer) delaySpinner.getValue();
        animationTimer = new Timer(delay, event -> advanceStep());
        animationTimer.setInitialDelay(0);
        animationTimer.start();
    }

    private void advanceStep() {
        if (steps == null || stepIndex >= steps.size()) {
            finishAnimation();
            return;
        }

        SortStep step = steps.get(stepIndex++);
        switch (step.type) {
            case COMPARE:
                comparisons++;
                visualizerPanel.setHighlight(step.indexA, step.indexB, StepType.COMPARE);
                setStatus("Comparing indices " + step.indexA + " and " + step.indexB + ".");
                break;
            case SWAP:
                swaps++;
                swap(visualArray, step.indexA, step.indexB);
                visualizerPanel.setHighlight(step.indexA, step.indexB, StepType.SWAP);
                setStatus("Swapping indices " + step.indexA + " and " + step.indexB + ".");
                break;
            case PIVOT:
                visualizerPanel.setPivotIndex(step.indexA);
                if (step.indexA >= 0) {
                    setStatus("Pivot set at index " + step.indexA + ".");
                }
                break;
            case DONE:
                finishAnimation();
                return;
            default:
                break;
        }

        updateMetricsLabel();
        visualizerPanel.repaint();
    }

    private void finishAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }

        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        visualizerPanel.clearHighlights();
        setControlsEnabled(true);

        if (activePlan != null) {
            sortedArea.setText(formatArray(activePlan.sortedArray));
            setStatus(activePlan.algorithmName + " complete. Sorted: " + activePlan.sortedOk + ".");
        } else {
            setStatus("Ready.");
        }

        updateMetricsLabel();
        activePlan = null;
    }

    private void togglePause() {
        if (animationTimer == null) {
            return;
        }

        if (animationTimer.isRunning()) {
            animationTimer.stop();
            pauseButton.setText("Resume");
            statusLabel.setText("Paused.");
        } else {
            animationTimer.start();
            pauseButton.setText("Pause");
            statusLabel.setText(runningStatus);
        }
    }

    private void updateTimerDelay() {
        if (animationTimer != null) {
            animationTimer.setDelay((Integer) delaySpinner.getValue());
        }
    }

    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        activePlan = null;
        steps = null;
        stepIndex = 0;
        comparisons = 0;
        swaps = 0;
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        if (visualizerPanel != null) {
            visualizerPanel.clearHighlights();
        }
    }

    private void setControlsEnabled(boolean enabled) {
        generateButton.setEnabled(enabled);
        sortButton.setEnabled(enabled);
        algorithmCombo.setEnabled(enabled);
        sizeSpinner.setEnabled(enabled);
        minSpinner.setEnabled(enabled);
        maxSpinner.setEnabled(enabled);
    }

    private void updateMetricsLabel() {
        if (steps == null || steps.isEmpty()) {
            metricsLabel.setText("Comparisons: " + comparisons + " | Swaps: " + swaps);
            return;
        }
        int totalSteps = steps.size();
        int currentStep = Math.min(stepIndex, totalSteps);
        metricsLabel.setText("Step " + currentStep + "/" + totalSteps +
            " | Comparisons: " + comparisons + " | Swaps: " + swaps);
    }

    private void setStatus(String message) {
        runningStatus = message;
        statusLabel.setText(message);
    }

    private SortPlan buildSortPlan(int algorithmIndex, int[] data) {
        List<SortStep> planSteps = new ArrayList<>();
        switch (algorithmIndex) {
            case 0:
                bubbleSortSteps(data, planSteps);
                break;
            case 1:
                quickSortSteps(data, 0, data.length - 1, planSteps);
                break;
            case 2:
                shakerSortSteps(data, planSteps);
                break;
            default:
                bubbleSortSteps(data, planSteps);
                break;
        }
        planSteps.add(SortStep.done());
        boolean sortedOk = TestEngine.isSorted(data);
        return new SortPlan(algorithms[algorithmIndex].getName(), planSteps, data, sortedOk);
    }

    private void bubbleSortSteps(int[] data, List<SortStep> planSteps) {
        int n = data.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                planSteps.add(SortStep.compare(j, j + 1));
                if (data[j] > data[j + 1]) {
                    swap(data, j, j + 1);
                    planSteps.add(SortStep.swap(j, j + 1));
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    private void shakerSortSteps(int[] data, List<SortStep> planSteps) {
        int left = 0;
        int right = data.length - 1;
        boolean swapped;

        while (left < right) {
            swapped = false;
            for (int i = left; i < right; i++) {
                planSteps.add(SortStep.compare(i, i + 1));
                if (data[i] > data[i + 1]) {
                    swap(data, i, i + 1);
                    planSteps.add(SortStep.swap(i, i + 1));
                    swapped = true;
                }
            }
            right--;
            if (!swapped) {
                break;
            }
            swapped = false;
            for (int i = right; i > left; i--) {
                planSteps.add(SortStep.compare(i - 1, i));
                if (data[i] < data[i - 1]) {
                    swap(data, i, i - 1);
                    planSteps.add(SortStep.swap(i - 1, i));
                    swapped = true;
                }
            }
            left++;
            if (!swapped) {
                break;
            }
        }
    }

    private void quickSortSteps(int[] data, int low, int high, List<SortStep> planSteps) {
        if (low < high) {
            int pivotIndex = partitionSteps(data, low, high, planSteps);
            planSteps.add(SortStep.pivot(-1));
            quickSortSteps(data, low, pivotIndex - 1, planSteps);
            quickSortSteps(data, pivotIndex + 1, high, planSteps);
        }
    }

    private int partitionSteps(int[] data, int low, int high, List<SortStep> planSteps) {
        int pivot = data[high];
        planSteps.add(SortStep.pivot(high));
        int i = low - 1;
        for (int j = low; j < high; j++) {
            planSteps.add(SortStep.compare(j, high));
            if (data[j] <= pivot) {
                i++;
                if (i != j) {
                    swap(data, i, j);
                    planSteps.add(SortStep.swap(i, j));
                }
            }
        }
        if (i + 1 != high) {
            swap(data, i + 1, high);
            planSteps.add(SortStep.swap(i + 1, high));
        }
        return i + 1;
    }

    private void swap(int[] data, int i, int j) {
        int temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    private String formatArray(int[] array) {
        if (array == null) {
            return "(no data)";
        }
        if (array.length == 0) {
            return "(empty)";
        }

        int limit = Math.min(array.length, MAX_DISPLAY);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limit; i++) {
            builder.append(array[i]);
            if (i < limit - 1) {
                builder.append(' ');
            }
            if ((i + 1) % PER_LINE == 0) {
                builder.append('\n');
            }
        }

        if (array.length > limit) {
            if (builder.length() > 0 && builder.charAt(builder.length() - 1) != '\n') {
                builder.append('\n');
            }
            builder.append("... showing ").append(limit).append(" of ").append(array.length).append(" values");
        }

        return builder.toString();
    }

    private enum StepType {
        COMPARE,
        SWAP,
        PIVOT,
        DONE
    }

    private static class SortStep {
        private final StepType type;
        private final int indexA;
        private final int indexB;

        private SortStep(StepType type, int indexA, int indexB) {
            this.type = type;
            this.indexA = indexA;
            this.indexB = indexB;
        }

        private static SortStep compare(int indexA, int indexB) {
            return new SortStep(StepType.COMPARE, indexA, indexB);
        }

        private static SortStep swap(int indexA, int indexB) {
            return new SortStep(StepType.SWAP, indexA, indexB);
        }

        private static SortStep pivot(int index) {
            return new SortStep(StepType.PIVOT, index, -1);
        }

        private static SortStep done() {
            return new SortStep(StepType.DONE, -1, -1);
        }
    }

    private static class SortPlan {
        private final String algorithmName;
        private final List<SortStep> steps;
        private final int[] sortedArray;
        private final boolean sortedOk;

        private SortPlan(String algorithmName, List<SortStep> steps, int[] sortedArray, boolean sortedOk) {
            this.algorithmName = algorithmName;
            this.steps = steps;
            this.sortedArray = sortedArray;
            this.sortedOk = sortedOk;
        }
    }

    private static class VisualizerPanel extends JPanel {
        private static final Color BASE_COLOR = new Color(78, 154, 131);
        private static final Color COMPARE_COLOR = new Color(233, 163, 79);
        private static final Color SWAP_COLOR = new Color(203, 78, 74);
        private static final Color PIVOT_COLOR = new Color(90, 120, 200);
        private static final Color FRAME_COLOR = new Color(224, 220, 214);
        private static final Color TEXT_COLOR = new Color(110, 110, 110);

        private int[] data;
        private int highlightA = -1;
        private int highlightB = -1;
        private int pivotIndex = -1;
        private StepType highlightType = StepType.COMPARE;

        private VisualizerPanel() {
            setBackground(new Color(253, 251, 248));
            setPreferredSize(new Dimension(620, 320));
        }

        private void setData(int[] data) {
            this.data = data;
            repaint();
        }

        private void setHighlight(int indexA, int indexB, StepType type) {
            this.highlightA = indexA;
            this.highlightB = indexB;
            this.highlightType = type;
            repaint();
        }

        private void setPivotIndex(int index) {
            this.pivotIndex = index;
            repaint();
        }

        private void clearHighlights() {
            highlightA = -1;
            highlightB = -1;
            pivotIndex = -1;
            highlightType = StepType.COMPARE;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data == null || data.length == 0) {
                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Serif", Font.PLAIN, 14));
                String message = "Generate data to visualize sorting.";
                int x = (getWidth() - g2.getFontMetrics().stringWidth(message)) / 2;
                int y = getHeight() / 2;
                g2.drawString(message, Math.max(12, x), y);
                g2.dispose();
                return;
            }

            int padding = 12;
            int width = getWidth() - padding * 2;
            int height = getHeight() - padding * 2;
            if (width <= 0 || height <= 0) {
                g2.dispose();
                return;
            }

            int min = data[0];
            int max = data[0];
            for (int value : data) {
                min = Math.min(min, value);
                max = Math.max(max, value);
            }

            boolean flat = min == max;
            int gap = data.length <= 60 ? 2 : 1;
            int barWidth = Math.max(1, (width - gap * (data.length - 1)) / data.length);
            int totalWidth = barWidth * data.length + gap * (data.length - 1);
            int startX = padding + Math.max(0, (width - totalWidth) / 2);

            g2.setColor(FRAME_COLOR);
            g2.drawRect(padding, padding, width, height);

            for (int i = 0; i < data.length; i++) {
                double ratio = flat ? 0.5 : (data[i] - min) / (double) (max - min);
                int barHeight = Math.max(1, (int) Math.round(ratio * height));
                int x = startX + i * (barWidth + gap);
                int y = padding + (height - barHeight);

                Color barColor = BASE_COLOR;
                if (i == pivotIndex) {
                    barColor = PIVOT_COLOR;
                }
                if (i == highlightA || i == highlightB) {
                    barColor = highlightType == StepType.SWAP ? SWAP_COLOR : COMPARE_COLOR;
                }

                g2.setColor(barColor);
                g2.fillRect(x, y, barWidth, barHeight);
            }

            g2.dispose();
        }
    }
}
