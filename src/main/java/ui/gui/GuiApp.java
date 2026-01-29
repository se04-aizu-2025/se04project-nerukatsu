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
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.Arrays;

public class GuiApp {
    private static final int MAX_DISPLAY = 1000;
    private static final int PER_LINE = 14;
    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#,##0.###");

    private final DataGenerator dataGenerator = new DataGenerator();
    private final SortAlgorithm[] algorithms = {
        new BubbleSort(),
        new QuickSort(),
        new ShakerSort()
    };

    private JFrame frame;
    private JComboBox<String> algorithmCombo;
    private JComboBox<String> arrayTypeCombo;
    private JSpinner sizeSpinner;
    private JSpinner minSpinner;
    private JSpinner maxSpinner;
    private JSlider speedSlider;
    private JTextArea unsortedArea;
    private JTextArea sortedArea;
    private SortVisualizer visualizer;
    private JLabel statusLabel;
    private JButton generateButton;
    private JButton sortButton;

    private int[] currentArray;

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

        frame.setMinimumSize(new Dimension(900, 600));
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

        JLabel subtitle = new JLabel("Generate a dataset, choose an algorithm, and compare results.");
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
        
        JPanel centerPanel = new JPanel(new BorderLayout(16, 16));
        centerPanel.setBackground(new Color(246, 242, 236));
        visualizer = new SortVisualizer();
        centerPanel.add(visualizer, BorderLayout.NORTH);
        centerPanel.add(buildDataPanel(), BorderLayout.CENTER);
        
        main.add(centerPanel, BorderLayout.CENTER);
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

        arrayTypeCombo = new JComboBox<>();
        for (DataGenerator.ArrayType type : DataGenerator.getAvailableTypes()) {
            arrayTypeCombo.addItem(type.getDisplayName());
        }
        arrayTypeCombo.setSelectedIndex(0); // RANDOM がデフォルト

        sizeSpinner = new JSpinner(new SpinnerNumberModel(40, 0, 100000, 1));
        minSpinner = new JSpinner(new SpinnerNumberModel(0, -100000, 100000, 1));
        maxSpinner = new JSpinner(new SpinnerNumberModel(100, -100000, 100000, 1));

        generateButton = new JButton("Generate");
        sortButton = new JButton("Sort");

        generateButton.addActionListener(event -> generateData());
        sortButton.addActionListener(event -> startSort());

        // アニメーション速度スライダーを追加
        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 5);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int speed = speedSlider.getValue();
                visualizer.setAnimationDelay(51 - speed);
            }
        });

        int row = 0;
        addControlRow(controls, gbc, row++, "Algorithm", algorithmCombo);
        addControlRow(controls, gbc, row++, "Array Type", arrayTypeCombo);
        addControlRow(controls, gbc, row++, "Array size", sizeSpinner);
        addControlRow(controls, gbc, row++, "Minimum", minSpinner);
        addControlRow(controls, gbc, row++, "Maximum", maxSpinner);
        addControlRow(controls, gbc, row++, "Speed", speedSlider);

        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controls.add(generateButton, gbc);

        gbc.gridy = row;
        controls.add(sortButton, gbc);

        return controls;
    }

    private void addControlRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JSlider slider) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(slider, gbc);
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
        unsortedArea = createDataArea();
        sortedArea = createDataArea();

        JPanel leftPanel = wrapArea("Unsorted", unsortedArea);
        JPanel rightPanel = wrapArea("Sorted", sortedArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);
        splitPane.setBorder(null);

        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBackground(new Color(246, 242, 236));
        dataPanel.add(splitPane, BorderLayout.CENTER);
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
        statusLabel = new JLabel("Ready.");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        return statusPanel;
    }

    private void generateData() {
        int size = (Integer) sizeSpinner.getValue();
        int min = (Integer) minSpinner.getValue();
        int max = (Integer) maxSpinner.getValue();

        if (min > max) {
            JOptionPane.showMessageDialog(frame, "Minimum must be <= maximum.", "Invalid range",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        DataGenerator.ArrayType arrayType = DataGenerator.getAvailableTypes()[arrayTypeCombo.getSelectedIndex()];
        currentArray = dataGenerator.generateArray(arrayType, size, min, max);
        unsortedArea.setText(formatArray(currentArray));
        sortedArea.setText("");
        visualizer.setData(currentArray, algorithms[algorithmCombo.getSelectedIndex()]);
        statusLabel.setText(String.format("Generated %s array with %,d values in [%d, %d].", 
            arrayType.getDisplayName(), size, min, max));
    }

    private void startSort() {
        if (currentArray == null) {
            JOptionPane.showMessageDialog(frame, "Generate an array first.", "No data",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // ソート中の場合は新しいソートを開始しない
        if (visualizer.isSorting()) {
            JOptionPane.showMessageDialog(frame, "Sorting is already in progress.", "Already Sorting",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SortAlgorithm algorithm = algorithms[algorithmCombo.getSelectedIndex()];
        int[] workingCopy = Arrays.copyOf(currentArray, currentArray.length);

        setButtonsEnabled(false);
        statusLabel.setText("Sorting with " + algorithm.getName() + "...");
        visualizer.setData(workingCopy, algorithm);

        SwingWorker<SortResult, Void> worker = new SwingWorker<SortResult, Void>() {
            @Override
            protected SortResult doInBackground() {
                long start = System.nanoTime();
                visualizer.startSorting();
                long end = System.nanoTime();
                boolean valid = TestEngine.isValidSort(currentArray, workingCopy);
                return new SortResult(workingCopy, valid, end - start);
            }

            @Override
            protected void done() {
                try {
                    SortResult result = get();
                    sortedArea.setText(formatArray(result.sorted));
                    statusLabel.setText(formatStatus(algorithm, result));
                } catch (Exception ex) {
                    statusLabel.setText("Sort failed.");
                    JOptionPane.showMessageDialog(frame, "Sort failed: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        generateButton.setEnabled(enabled);
        sortButton.setEnabled(enabled);
        algorithmCombo.setEnabled(enabled);
        arrayTypeCombo.setEnabled(enabled);
        sizeSpinner.setEnabled(enabled);
        minSpinner.setEnabled(enabled);
        maxSpinner.setEnabled(enabled);
        speedSlider.setEnabled(enabled);
    }

    private String formatStatus(SortAlgorithm algorithm, SortResult result) {
        double ms = result.durationNanos / 1_000_000.0;
        return algorithm.getName() + " completed in " + TIME_FORMAT.format(ms) + " ms. Valid: " + result.sortedOk;
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

    private static class SortResult {
        private final int[] sorted;
        private final boolean sortedOk;
        private final long durationNanos;

        private SortResult(int[] sorted, boolean sortedOk, long durationNanos) {
            this.sorted = sorted;
            this.sortedOk = sortedOk;
            this.durationNanos = durationNanos;
        }
    }
}
