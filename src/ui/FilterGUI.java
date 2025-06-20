package ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import filters.*;
import processor.ImageProcessor;
import processor.Timer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FilterGUI extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage resultImage;
    private JLabel originalLabel = new JLabel();
    private JLabel resultLabel = new JLabel();
    private JLabel infoLabel = new JLabel("ℹ Load an image to begin.");
    
    // Enhanced timing display components
    private JLabel sequentialTimeLabel = new JLabel("--");
    private JLabel parallelTimeLabel = new JLabel("--");
    private JLabel speedupLabel = new JLabel("--");
    private JProgressBar performanceBar = new JProgressBar(0, 100);
    private JPanel timingPanel;

    // Enhanced threshold components
    private JPanel thresholdPanel;
    private JTextField thresholdField;
    private JSlider thresholdSlider;
    private JLabel thresholdValueLabel;
    private JLabel thresholdRecommendationLabel;

    private double timeSeq = 0;
    private double timePar = 0;

    private JComboBox<String> filterSelector;
    private JButton zoomInBtn, zoomOutBtn;
    private float zoomFactor = 1.0f;

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private final Color SUCCESS_COLOR = new Color(46, 125, 50);
    private final Color WARNING_COLOR = new Color(255, 152, 0);
    private final Color ERROR_COLOR = new Color(211, 47, 47);
    private final Color ACCENT_COLOR = new Color(156, 39, 176);

    public FilterGUI() {
        super("🧠 Parallel Image Filtering Framework");
        FlatLightLaf.setup();
        initUI();
    }

    private void initUI() {
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        addMenuBar();
        addControlPanel();
        addImagePanels();
        setVisible(true);
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("🎨 Theme");

        JMenuItem light = new JMenuItem("☀️ Light Mode");
        JMenuItem dark = new JMenuItem("🌙 Dark Mode");

        light.addActionListener(e -> {
            FlatLightLaf.setup();
            SwingUtilities.updateComponentTreeUI(this);
        });

        dark.addActionListener(e -> {
            FlatDarkLaf.setup();
            SwingUtilities.updateComponentTreeUI(this);
        });

        themeMenu.add(light);
        themeMenu.add(dark);
        menuBar.add(themeMenu);
        setJMenuBar(menuBar);
    }

    private void addControlPanel() {
        JPanel control = new JPanel();
        control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));
        control.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        control.setPreferredSize(new Dimension(350, 0));

        // Load image section
        JPanel loadSection = createSection("📂 Image Loading", createLoadPanel());
        
        // Filter selection section
        JPanel filterSection = createSection("🔧 Filter Configuration", createFilterPanel());
        
        // Threshold configuration section
        JPanel thresholdSection = createSection("⚙️ Parallel Processing Threshold", createThresholdPanel());
        
        // Execution section
        JPanel executionSection = createSection("▶️ Execution", createExecutionPanel());
        
        // Performance metrics section
        JPanel performanceSection = createSection("📊 Performance Metrics", createPerformancePanel());
        
        // Zoom controls section
        JPanel zoomSection = createSection("🔍 View Controls", createZoomPanel());

        control.add(loadSection);
        control.add(Box.createVerticalStrut(10));
        control.add(filterSection);
        control.add(Box.createVerticalStrut(10));
        control.add(thresholdSection);
        control.add(Box.createVerticalStrut(10));
        control.add(executionSection);
        control.add(Box.createVerticalStrut(10));
        control.add(performanceSection);
        control.add(Box.createVerticalStrut(10));
        control.add(zoomSection);
        control.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(control);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.WEST);
    }

    private JPanel createSection(String title, JPanel content) {
        JPanel section = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        border.setTitleColor(PRIMARY_COLOR);
        section.setBorder(BorderFactory.createCompoundBorder(
            border,
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        section.add(content, BorderLayout.CENTER);
        return section;
    }

    private JPanel createLoadPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadBtn = new JButton("📂 Load Image");
        loadBtn.setBackground(PRIMARY_COLOR);
        loadBtn.setForeground(Color.WHITE);
        loadBtn.setFocusPainted(false);
        loadBtn.addActionListener(e -> loadImage());
        
        panel.add(loadBtn);
        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        filterSelector = new JComboBox<>(new String[]{"Gaussian Blur", "Sharpen", "Sobel Edge"});
        filterSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(filterSelector, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createThresholdPanel() {
        thresholdPanel = new JPanel();
        thresholdPanel.setLayout(new BoxLayout(thresholdPanel, BoxLayout.Y_AXIS));

        // Threshold input row
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        inputRow.add(new JLabel("Value: "));
        thresholdField = new JTextField("auto", 8);
        thresholdField.addActionListener(e -> updateThresholdFromField());
        inputRow.add(thresholdField);
        
        thresholdValueLabel = new JLabel(" (Auto)");
        thresholdValueLabel.setForeground(PRIMARY_COLOR);
        thresholdValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        inputRow.add(thresholdValueLabel);

        // Threshold slider
        thresholdSlider = new JSlider(1, 500, 100);
        thresholdSlider.setEnabled(false);
        thresholdSlider.addChangeListener(e -> updateThresholdFromSlider());
        
        // Recommendation label
        thresholdRecommendationLabel = new JLabel("💡 Load image for auto-recommendation");
        thresholdRecommendationLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        thresholdRecommendationLabel.setForeground(new Color(100, 100, 100));

        thresholdPanel.add(inputRow);
        thresholdPanel.add(Box.createVerticalStrut(5));
        thresholdPanel.add(thresholdSlider);
        thresholdPanel.add(Box.createVerticalStrut(5));
        thresholdPanel.add(thresholdRecommendationLabel);

        return thresholdPanel;
    }

    private JPanel createExecutionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton runBothBtn = new JButton("▶ Run Both Modes");
        runBothBtn.setBackground(SUCCESS_COLOR);
        runBothBtn.setForeground(Color.WHITE);
        runBothBtn.setFocusPainted(false);
        runBothBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        runBothBtn.addActionListener(e -> runBothFilters());

        JButton saveBtn = new JButton("💾 Save");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> saveImage());

        panel.add(runBothBtn);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(saveBtn);
        return panel;
    }

    private JPanel createPerformancePanel() {
        timingPanel = new JPanel();
        timingPanel.setLayout(new BoxLayout(timingPanel, BoxLayout.Y_AXIS));

        // Sequential time
        JPanel seqPanel = new JPanel(new BorderLayout());
        seqPanel.add(new JLabel("🔄 Sequential: "), BorderLayout.WEST);
        sequentialTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sequentialTimeLabel.setForeground(WARNING_COLOR);
        seqPanel.add(sequentialTimeLabel, BorderLayout.CENTER);

        // Parallel time
        JPanel parPanel = new JPanel(new BorderLayout());
        parPanel.add(new JLabel("⚡ Parallel: "), BorderLayout.WEST);
        parallelTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        parallelTimeLabel.setForeground(SUCCESS_COLOR);
        parPanel.add(parallelTimeLabel, BorderLayout.CENTER);

        // Speedup display
        JPanel speedupPanel = new JPanel(new BorderLayout());
        speedupPanel.add(new JLabel("🚀 Speedup: "), BorderLayout.WEST);
        speedupLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        speedupLabel.setForeground(ACCENT_COLOR);
        speedupPanel.add(speedupLabel, BorderLayout.CENTER);

        // Performance bar
        performanceBar.setStringPainted(true);
        performanceBar.setString("No data");
        performanceBar.setForeground(SUCCESS_COLOR);
        performanceBar.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.BLACK;
            }
            @Override
            protected Color getSelectionBackground() {
                return Color.BLACK;
            }
        });

        // Info label
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(80, 80, 80));

        timingPanel.add(seqPanel);
        timingPanel.add(Box.createVerticalStrut(5));
        timingPanel.add(parPanel);
        timingPanel.add(Box.createVerticalStrut(5));
        timingPanel.add(speedupPanel);
        timingPanel.add(Box.createVerticalStrut(8));
        timingPanel.add(performanceBar);
        timingPanel.add(Box.createVerticalStrut(8));
        timingPanel.add(infoLabel);

        return timingPanel;
    }

    private JPanel createZoomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        zoomInBtn = new JButton("🔍+");
        zoomOutBtn = new JButton("🔍-");
        
        zoomInBtn.setPreferredSize(new Dimension(50, 30));
        zoomOutBtn.setPreferredSize(new Dimension(50, 30));
        
        zoomInBtn.setBackground(new Color(230, 230, 230));
        zoomOutBtn.setBackground(new Color(230, 230, 230));
        
        zoomInBtn.addActionListener(e -> changeZoom(0.1f));
        zoomOutBtn.addActionListener(e -> changeZoom(-0.1f));

        panel.add(zoomOutBtn);
        panel.add(zoomInBtn);
        return panel;
    }

    private void updateThresholdFromField() {
        String text = thresholdField.getText().trim().toLowerCase();
        if (!text.equals("auto") && !text.isEmpty()) {
            try {
                int value = Integer.parseInt(text);
                thresholdSlider.setValue(Math.min(Math.max(value, 1), 500));
                thresholdValueLabel.setText(" (" + value + ")");
                thresholdValueLabel.setForeground(PRIMARY_COLOR);
            } catch (NumberFormatException e) {
                thresholdValueLabel.setText(" (Invalid)");
                thresholdValueLabel.setForeground(ERROR_COLOR);
            }
        } else {
            thresholdValueLabel.setText(" (Auto)");
            thresholdValueLabel.setForeground(PRIMARY_COLOR);
        }
    }

    private void updateThresholdFromSlider() {
        if (thresholdSlider.getValueIsAdjusting()) return;
        int value = thresholdSlider.getValue();
        thresholdField.setText(String.valueOf(value));
        thresholdValueLabel.setText(" (" + value + ")");
        thresholdValueLabel.setForeground(PRIMARY_COLOR);
    }

    private void addImagePanels() {
        originalLabel.setHorizontalAlignment(JLabel.CENTER);
        resultLabel.setHorizontalAlignment(JLabel.CENTER);
        originalLabel.setText("Original Image");
        resultLabel.setText("Filtered Result");

        JScrollPane originalPane = new JScrollPane(originalLabel);
        JScrollPane resultPane = new JScrollPane(resultLabel);
        
        originalPane.setBorder(BorderFactory.createTitledBorder("Original Image"));
        resultPane.setBorder(BorderFactory.createTitledBorder("Filtered Result"));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, originalPane, resultPane);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                originalImage = ImageProcessor.loadImage(file.getAbsolutePath());
                originalLabel.setIcon(getScaledIcon(originalImage));
                originalLabel.setText("");
                resultLabel.setIcon(null);
                resultLabel.setText("Filtered Result");
                resultImage = null;
                zoomFactor = 1.0f;
                
                // Update threshold recommendation
                int recommendedThreshold = Math.max(20, originalImage.getHeight() / 12);
                thresholdSlider.setEnabled(true);
                thresholdSlider.setMaximum(Math.max(500, originalImage.getHeight()));
                thresholdRecommendationLabel.setText("💡 Recommended: " + recommendedThreshold + " (based on image height)");
                thresholdRecommendationLabel.setForeground(SUCCESS_COLOR);
                
                infoLabel.setText("✅ Loaded: " + originalImage.getWidth() + "×" + originalImage.getHeight() + " pixels");
                resetPerformanceDisplay();
                
            } catch (Exception ex) {
                showError("Could not load image: " + ex.getMessage());
            }
        }
    }

    private void runBothFilters() {
        if (originalImage == null) {
            showError("Please load an image first.");
            return;
        }

        Filter filter = switch ((String) filterSelector.getSelectedItem()) {
            case "Gaussian Blur" -> new GaussianBlurFilter();
            case "Sharpen" -> new SharpenFilter();
            case "Sobel Edge" -> new SobelEdgeFilter();
            default -> throw new IllegalArgumentException("Invalid filter");
        };

        int threshold;
        String threshInput = thresholdField.getText().trim().toLowerCase();

        if (threshInput.isEmpty() || threshInput.equals("auto") || threshInput.equals("0")) {
            threshold = Math.max(20, originalImage.getHeight() / 12);
            thresholdField.setText(String.valueOf(threshold));
            thresholdSlider.setValue(threshold);
            updateThresholdFromField();
        } else {
            try {
                threshold = Integer.parseInt(threshInput);
            } catch (Exception e) {
                threshold = 100;
                thresholdField.setText("100");
                thresholdSlider.setValue(100);
                updateThresholdFromField();
            }
        }

        // Show processing status
        infoLabel.setText("🔄 Processing...");
        sequentialTimeLabel.setText("Running...");
        parallelTimeLabel.setText("Running...");
        speedupLabel.setText("Computing...");
        performanceBar.setString("Processing...");

        Timer timer = new Timer();

        // Sequential execution
        timer.start();
        ImageProcessor.applySequential(originalImage, filter);
        timer.start();
        ImageProcessor.applySequential(originalImage, filter);
        timeSeq = timer.stopAndGetSeconds();

        // Parallel execution
        timer.start();
        ImageProcessor.applyParallel(originalImage, filter, threshold);
        timer.start();
        resultImage = ImageProcessor.applyParallel(originalImage, filter, threshold);
        timePar = timer.stopAndGetSeconds();

        resultLabel.setIcon(getScaledIcon(resultImage));
        resultLabel.setText("");

        updatePerformanceDisplay();
    }

    private void updatePerformanceDisplay() {
        sequentialTimeLabel.setText(String.format("%.3f s", timeSeq));
        parallelTimeLabel.setText(String.format("%.3f s", timePar));
        
        double speedup = timeSeq / timePar;
        speedupLabel.setText(String.format("%.2fx", speedup));
        
        // Update performance bar
        int efficiency = (int) Math.min(100, (speedup - 1) * 50); // Scale speedup to 0-100
        performanceBar.setValue(efficiency);
        performanceBar.setString(String.format("%.1fx speedup", speedup));
        
        if (speedup > 2.0) {
            performanceBar.setForeground(SUCCESS_COLOR);
        } else if (speedup > 1.2) {
            performanceBar.setForeground(WARNING_COLOR);
        } else {
            performanceBar.setForeground(ERROR_COLOR);
        }
        
        infoLabel.setText(String.format("✅ Processing complete | Filter: %s | Threshold: %s", 
            filterSelector.getSelectedItem(), thresholdField.getText()));
    }

    private void resetPerformanceDisplay() {
        sequentialTimeLabel.setText("--");
        parallelTimeLabel.setText("--");
        speedupLabel.setText("--");
        performanceBar.setValue(0);
        performanceBar.setString("No data");
        performanceBar.setForeground(new Color(200, 200, 200));
    }

    private void saveImage() {
        if (resultImage == null) {
            showError("No processed image to save. Please run a filter first.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageProcessor.saveImage(resultImage, chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showError("Failed to save image: " + ex.getMessage());
            }
        }
    }

    private void changeZoom(float delta) {
        zoomFactor = Math.max(0.1f, Math.min(3.0f, zoomFactor + delta));
        if (originalImage != null) {
            originalLabel.setIcon(getScaledIcon(originalImage));
        }
        if (resultImage != null) {
            resultLabel.setIcon(getScaledIcon(resultImage));
        }
    }

    private ImageIcon getScaledIcon(BufferedImage image) {
        int newW = (int) (image.getWidth() * zoomFactor);
        int newH = (int) (image.getHeight() * zoomFactor);
        Image scaled = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FilterGUI::new);
    }
}