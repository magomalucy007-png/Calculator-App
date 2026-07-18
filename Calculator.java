import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;
public class Calculator extends JFrame implements ActionListener {
    private final JTextField display;
    private final JTextField resultField;
    private final JTextArea stepsArea;
    private final StringBuilder expression = new StringBuilder("0");
    private boolean startNewNumber = true;
    private double lastResult = 0.0;
    private boolean hasLastResult = false;
    private double memory = 0.0;
    private boolean hasMemory = false;
    private String lastExpression = "";
    private String lastResultText = "";
    private boolean darkMode = false;
    private boolean useDegrees = true;
    private boolean invMode = false;
    private JButton degRadButton;
    private JButton invToggleButton;
    private JPanel scientificPanel;
    private JPanel keypadPanel;
    private JPanel historyPanel;
    private JList<HistoryEntry> historyListView;
    private DefaultListModel<HistoryEntry> historyListModel;
    private JTextArea historyStepsArea;
    private JTextArea formulaArea;
    private JButton themeToggleButton;
    private JLabel memoryLabel;
    private JLabel statusLabel;
    private JLabel challengeLabel;
    private JLabel challengeStatusLabel;
    private JList<String> formulaList;
    private DefaultListModel<String> formulaListModel;
    private JComboBox<String> formulaFieldsCombo;
    private JButton challengeButton;
    private final List<HistoryEntry> historyList = new ArrayList<>();
    private final File historyFile = resolveHistoryFile();
    private GradientPanel rootPanel;
    private int challengeScore = 0;
    private int challengeAttempts = 0;
    private Challenge currentChallenge;
    private final Random random = new Random();
    private boolean showSteps = true;

    private final String[] scientificButtons = {"sin", "cos", "tan", "√", "log", "ln", "π", "e", "Ans", "x²", "(", ")"};
    private final String[] keypadButtons = {"7", "8", "9", "/", "C", "4", "5", "6", "*", "←", "1", "2", "3", "-", "^", "0", ".", "=", "+", "%"};

    public Calculator() {
        super("QuantCalc");
        setIconImage(Toolkit.getDefaultToolkit().getImage("quantcalc-icon.png"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 720);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.NORMAL);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new GlassPanel(new Color(255, 255, 255, 140), new Color(255, 255, 255, 200), 30);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new GlassPanel(new Color(255, 255, 255, 120), new Color(255, 255, 255, 220), 24);
        topPanel.setLayout(new BorderLayout(8, 8));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        display = new JTextField("0");
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setFont(new Font("Segoe UI", Font.BOLD, 22));
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 220), 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        resultField = new JTextField("");
        resultField.setHorizontalAlignment(SwingConstants.RIGHT);
        resultField.setEditable(false);
        resultField.setFont(new Font("Segoe UI", Font.BOLD, 28));
        resultField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 220), 1, true),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JPanel displayPanel = new JPanel(new BorderLayout(6, 6));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel titlePanel = new JPanel(new BorderLayout(10, 4));
        titlePanel.setOpaque(false);
        JLabel iconLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("quantcalc-icon.png");
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        } catch (Exception ignored) {
        }
        JPanel titleTextPanel = new JPanel(new BorderLayout(2, 2));
        titleTextPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("QuantCalc");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        titleLabel.setForeground(new Color(34, 37, 55));
        JLabel tagLine = new JLabel("Smart Scientific Calculator & Learning Assistant");
        tagLine.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagLine.setForeground(new Color(80, 84, 108));
        titleTextPanel.add(titleLabel, BorderLayout.NORTH);
        titleTextPanel.add(tagLine, BorderLayout.SOUTH);
        titlePanel.add(iconLabel, BorderLayout.WEST);
        titlePanel.add(titleTextPanel, BorderLayout.CENTER);
        displayPanel.add(titlePanel, BorderLayout.NORTH);
        Box v = Box.createVerticalBox();
        JLabel exprLabel = new JLabel("Expression");
        exprLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exprLabel.setBorder(BorderFactory.createEmptyBorder(2,2,6,2));
        v.add(exprLabel);
        v.add(display);
        v.add(Box.createVerticalStrut(6));
        JLabel resLabel = new JLabel("Result");
        resLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resLabel.setBorder(BorderFactory.createEmptyBorder(2,2,6,2));
        v.add(resLabel);
        v.add(resultField);
        displayPanel.add(v, BorderLayout.CENTER);
        topPanel.add(displayPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        String[] memoryButtons = {"MC", "MR", "M+", "M-"};
        for (String label : memoryButtons) {
            JButton button = new JButton(label);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.addActionListener(this);
            button.setFocusable(false);
            controlPanel.add(button);
        }

        JButton formulaButton = new JButton("Use Formula");
        formulaButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formulaButton.addActionListener(this);
        formulaButton.setFocusable(false);
        controlPanel.add(formulaButton);

        JButton explainButton = new JButton("Explain");
        explainButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        explainButton.addActionListener(this);
        explainButton.setFocusable(false);
        controlPanel.add(explainButton);

        JButton graphButton = new JButton("Graph");
        graphButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        graphButton.addActionListener(this);
        graphButton.setFocusable(false);
        controlPanel.add(graphButton);

        challengeButton = new JButton("Challenge Me");
        challengeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        challengeButton.addActionListener(this);
        challengeButton.setFocusable(false);
        controlPanel.add(challengeButton);

        themeToggleButton = new JButton("🌙");
        themeToggleButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        themeToggleButton.addActionListener(this);
        themeToggleButton.setFocusable(false);
        controlPanel.add(themeToggleButton);
        degRadButton = new JButton("Deg");
        degRadButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        degRadButton.addActionListener(this);
        degRadButton.setFocusable(false);
        controlPanel.add(degRadButton);

        invToggleButton = new JButton("Inv");
        invToggleButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        invToggleButton.addActionListener(this);
        invToggleButton.setFocusable(false);
        controlPanel.add(invToggleButton);

        JButton expandButton = new JButton("Expand");
        expandButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        expandButton.addActionListener(this);
        expandButton.setFocusable(false);
        controlPanel.add(expandButton);

        memoryLabel = new JLabel("M: 0");
        memoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        controlPanel.add(memoryLabel);
        topPanel.add(controlPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel workspacePanel = new JPanel(new BorderLayout(10, 10));

        JPanel calculatorPanel = new GlassPanel(new Color(255, 255, 255, 90), new Color(255, 255, 255, 180), 22);
        calculatorPanel.setLayout(new BorderLayout(8, 8));
        calculatorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scientificPanel = new GlassPanel(new Color(255, 255, 255, 70), new Color(255, 255, 255, 170), 18);
        scientificPanel.setLayout(new GridLayout(2, 6, 6, 6));
        scientificPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 6, 4));
        for (String label : scientificButtons) {
            addButton(scientificPanel, label);
        }
        calculatorPanel.add(scientificPanel, BorderLayout.NORTH);

        keypadPanel = new GlassPanel(new Color(255, 255, 255, 70), new Color(255, 255, 255, 170), 18);
        keypadPanel.setLayout(new GridLayout(4, 5, 6, 6));
        keypadPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        for (String label : keypadButtons) {
            addButton(keypadPanel, label);
        }
        calculatorPanel.add(keypadPanel, BorderLayout.CENTER);
        workspacePanel.add(calculatorPanel, BorderLayout.CENTER);

        historyPanel = new GlassPanel(new Color(255, 255, 255, 100), new Color(255, 255, 255, 200), 22);
        historyPanel.setLayout(new BorderLayout(6, 6));
        // Use a simple line border + padding; the title will be shown as a separate label above the panel
        historyPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 195, 220)), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        historyPanel.setPreferredSize(new Dimension(300, 0));
        historyListModel = new DefaultListModel<>();
        historyListView = new JList<>(historyListModel);
        historyListView.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyListView.setVisibleRowCount(8);
        historyListView.setSelectionBackground(new Color(120, 77, 255));
        historyListView.setSelectionForeground(Color.WHITE);
        historyListView.setToolTipText("Click an entry to view its steps.");
        historyListView.setFixedCellHeight(64);
        historyListView.setCellRenderer(new ListCellRenderer<HistoryEntry>() {
            private final JLabel label = new JLabel();

            @Override
            public Component getListCellRendererComponent(JList<? extends HistoryEntry> list, HistoryEntry value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    label.setText("");
                } else {
                    String h = toHtmlPreview(value.header, list.getWidth() - 24);
                    label.setText(h);
                }
                label.setOpaque(true);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                label.setVerticalAlignment(SwingConstants.TOP);
                label.setVerticalTextPosition(SwingConstants.TOP);
                label.setPreferredSize(new Dimension(list.getWidth() - 20, 64));
                if (isSelected) {
                    label.setBackground(new Color(120, 77, 255));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
                return label;
            }
        });
        historyListView.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                updateHistoryDetail();
            }
        });
        historyListView.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent ev) {
                updateHistoryDetail();
            }
        });
        JScrollPane historyListScroll = new JScrollPane(historyListView);
        historyPanel.setPreferredSize(new Dimension(420, 0));

        // Combobox panel for formula field selection
        JPanel fieldSelectorPanel = new GlassPanel(new Color(255, 255, 255, 70), new Color(255, 255, 255, 170), 16);
        fieldSelectorPanel.setLayout(new BorderLayout(6, 6));
        fieldSelectorPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        fieldSelectorPanel.setPreferredSize(new Dimension(420, 50));
        
        formulaFieldsCombo = new JComboBox<>();
        formulaFieldsCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formulaFieldsCombo.addActionListener(ev -> updateFormulaFieldsDisplay());
        
        JButton applyFormulaButton = new JButton("Apply Formula");
        applyFormulaButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        applyFormulaButton.addActionListener(this);
        applyFormulaButton.setFocusable(false);
        
        JPanel fieldComboPanel = new JPanel(new BorderLayout(6, 0));
        fieldComboPanel.setOpaque(false);
        fieldComboPanel.add(formulaFieldsCombo, BorderLayout.CENTER);
        fieldComboPanel.add(applyFormulaButton, BorderLayout.EAST);
        
        fieldSelectorPanel.add(fieldComboPanel, BorderLayout.CENTER);

        formulaArea = new JTextArea();
        formulaArea.setEditable(false);
        formulaArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formulaArea.setLineWrap(true);
        formulaArea.setWrapStyleWord(true);
        formulaArea.setBackground(new Color(240, 245, 250));
        formulaArea.setForeground(new Color(34, 37, 55));
        formulaArea.setText("Select a formula category to view available fields.");
        JScrollPane formulaScroll = new JScrollPane(formulaArea);
        formulaScroll.setPreferredSize(new Dimension(0, 140));

        // Challenge panel
        JPanel challengePanel = new GlassPanel(new Color(255, 255, 255, 70), new Color(255, 255, 255, 170), 16);
        challengePanel.setLayout(new BorderLayout(6, 6));
        challengePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        challengePanel.setPreferredSize(new Dimension(420, 120));

        Box challengeBox = Box.createVerticalBox();
        challengeLabel = new JLabel("Challenge: ready");
        challengeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        challengeLabel.setForeground(new Color(34, 37, 55));
        
        challengeStatusLabel = new JLabel("Score: 0/0");
        challengeStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        statusLabel = new JLabel("Ready to calculate");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        challengeBox.add(challengeLabel);
        challengeBox.add(Box.createVerticalStrut(4));
        challengeBox.add(challengeStatusLabel);
        challengeBox.add(Box.createVerticalStrut(4));
        challengeBox.add(statusLabel);
        
        challengePanel.add(challengeBox, BorderLayout.WEST);

        // Assemble history panel with new layout
        historyPanel.add(historyListScroll, BorderLayout.NORTH);
        historyPanel.add(fieldSelectorPanel, BorderLayout.CENTER);
        
        // Create a south panel with formula area and challenge panel
        JPanel southPanel = new JPanel(new BorderLayout(6, 6));
        southPanel.setOpaque(false);
        southPanel.add(formulaScroll, BorderLayout.NORTH);
        southPanel.add(challengePanel, BorderLayout.CENTER);
        
        historyPanel.add(southPanel, BorderLayout.SOUTH);
        // Left: formula library
        JPanel leftPanel = new GlassPanel(new Color(255,255,255,80), new Color(255,255,255,180), 20);
        leftPanel.setLayout(new BorderLayout(6,6));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Formulas"), BorderFactory.createEmptyBorder(6,6,6,6)));
        DefaultListModel<String> catModel = new DefaultListModel<>();
        catModel.addElement("Geometry");
        catModel.addElement("Algebra");
        catModel.addElement("Finance");
        catModel.addElement("Physics");
        catModel.addElement("Statistics");
        JList<String> catList = new JList<>(catModel);
        catList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catList.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                String sel = catList.getSelectedValue();
                if (sel != null) showFormulasForCategory(sel);
            }
        });
        leftPanel.add(new JScrollPane(catList), BorderLayout.CENTER);
        // default to Algebra so users see algebra formulas on open
        catList.setSelectedIndex(1);
        leftPanel.setPreferredSize(new Dimension(160,0));
        // Wrap the history panel with a header label so the title is always visible
        JPanel historyWrapper = new JPanel(new BorderLayout());
        historyWrapper.setOpaque(false);
        JLabel historyTitleLabel = new JLabel("Solution History");
        historyTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        historyTitleLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        historyTitleLabel.setForeground(new Color(34, 37, 55));
        historyWrapper.add(historyTitleLabel, BorderLayout.NORTH);
        historyWrapper.add(historyPanel, BorderLayout.CENTER);

        // Use a resizable split pane so the history panel remains visible and user-adjustable
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, calculatorPanel, historyWrapper);
        centerSplit.setResizeWeight(0.68);
        centerSplit.setContinuousLayout(true);
        centerSplit.setOneTouchExpandable(true);
        centerSplit.setBorder(null);
        workspacePanel.add(leftPanel, BorderLayout.WEST);
        workspacePanel.add(centerSplit, BorderLayout.CENTER);

        mainPanel.add(workspacePanel, BorderLayout.CENTER);
        rootPanel = new GradientPanel();
        rootPanel.setLayout(new BorderLayout(10, 10));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        rootPanel.add(mainPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                display.requestFocusInWindow();
            }
        });

        bindKeyboardShortcuts();
        applyTheme();
        loadHistoryFromFile();
        showWelcomeMessage();
        setVisible(true);
    }

    private void showWelcomeMessage() {
        String html = "<html><div style='font-family:Segoe UI, Arial; text-align:left;'>"
                + "<h1 style='font-size:22pt; margin:6px 0 6px 0;'>Welcome to QuantCalc!</h1>"
                + "<p style='font-size:12pt; margin:6px 0;'>Your intelligent scientific calculator.</p>"
                + "<ul style='font-size:11pt; margin:6px 0 0 18px;'>"
                + "<li>Scientific Functions</li>"
                + "<li>Formula Library</li>"
                + "<li>Step Solver</li>"
                + "<li>Graphing</li>"
                + "</ul>"
                + "</div></html>";
        JLabel label = new JLabel(html);
        JOptionPane.showMessageDialog(this, label, "Welcome to QuantCalc!", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showFormulasForCategory(String category) {
        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        
        switch (category) {
            case "Geometry" -> {
                comboModel.addElement("Circle");
                comboModel.addElement("Rectangle");
                comboModel.addElement("Triangle");
                comboModel.addElement("Cylinder");
                comboModel.addElement("Sphere");
            }
            case "Algebra" -> {
                comboModel.addElement("Quadratic equation");
                comboModel.addElement("Pythagoras");
                comboModel.addElement("Circle");
                comboModel.addElement("Rectangle");
            }
            case "Finance" -> {
                comboModel.addElement("Simple Interest");
                comboModel.addElement("Compound Interest");
                comboModel.addElement("Loan Payment");
            }
            case "Physics" -> {
                comboModel.addElement("Force");
                comboModel.addElement("Velocity");
                comboModel.addElement("Energy");
            }
            case "Statistics" -> {
                comboModel.addElement("Mean");
                comboModel.addElement("Standard Deviation");
                comboModel.addElement("Probability");
            }
            default -> {
                formulaArea.setText(category + " formulas coming soon.");
                formulaFieldsCombo.setModel(new DefaultComboBoxModel<>());
                return;
            }
        }
        
        formulaFieldsCombo.setModel(comboModel);
        
        if (comboModel.getSize() > 0) {
            formulaFieldsCombo.setSelectedIndex(0);
            updateFormulaFieldsDisplay();
        }
    }
    
    private void updateFormulaFieldsDisplay() {
        if (formulaFieldsCombo.getSelectedItem() != null) {
            String selected = (String) formulaFieldsCombo.getSelectedItem();
            showFormulaDetails(selected);
        }
    }

    private void showFormulaDetails(String formula) {
        if (formula == null) {
            formulaArea.setText("");
            return;
        }
        switch (formula) {
            case "Circle" -> formulaArea.setText("Circle area: A = πr²\nSelect Apply Formula to compute using radius.");
            case "Rectangle" -> formulaArea.setText("Rectangle area: A = l × w\nSelect Apply Formula to compute using length and width.");
            case "Triangle" -> formulaArea.setText("Triangle area: A = ½ × base × height\nSelect Apply Formula to compute using base and height.");
            case "Cylinder" -> formulaArea.setText("Cylinder volume: V = πr²h\nSelect Apply Formula to compute using radius and height.");
            case "Sphere" -> formulaArea.setText("Sphere volume: V = 4/3 πr³\nSelect Apply Formula to compute using radius.");
            case "Pythagoras" -> formulaArea.setText("Pythagoras: c = √(a² + b²)\nSelect Apply Formula to compute the hypotenuse.");
            case "Quadratic equation" -> formulaArea.setText("Quadratic formula: x = (-b ± √(b² - 4ac)) / 2a\nSelect Apply Formula to compute equation roots.");
            case "Simple Interest" -> formulaArea.setText("Simple Interest: SI = P × R × T / 100\nSelect Apply Formula to compute interest.");
            case "Compound Interest" -> formulaArea.setText("Compound Interest: A = P(1 + r/n)^(nt)\nSelect Apply Formula to compute amount.");
            case "Loan Payment" -> formulaArea.setText("Loan Payment: Use standard amortization formula.\nThis option is available in later updates.");
            case "Force" -> formulaArea.setText("Force: F = m × a\nSelect Apply Formula to compute force.");
            case "Velocity" -> formulaArea.setText("Velocity: v = d / t\nSelect Apply Formula to compute velocity.");
            case "Energy" -> formulaArea.setText("Energy: E = mc²\nSelect Apply Formula to compute energy.");
            case "Mean" -> formulaArea.setText("Mean: sum(values) / count\nUse the expression interface or enter values manually in the calculator.");
            case "Standard Deviation" -> formulaArea.setText("Standard Deviation: Use the calculator to compute variance and root of the average squared deviation.");
            case "Probability" -> formulaArea.setText("Probability: p = favorable / total\nUse the expression interface to compute probabilities.");
            default -> formulaArea.setText(formula + " is available for calculation.");
        }
    }

    private void addButton(JPanel panel, String label) {
        JButton button = new RoundedButton(label);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(16, new Color(190, 200, 212), 1));
        button.addActionListener(this);
        button.setFocusable(false);
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton button) {
            handleInput(button.getText());
        }
    }

    private void bindKeyboardShortcuts() {
        JComponent root = (JComponent) getContentPane();
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        for (char ch = '0'; ch <= '9'; ch++) {
            final char currentDigit = ch;
            String key = "typed " + currentDigit;
            inputMap.put(KeyStroke.getKeyStroke(key), "digit-" + currentDigit);
            actionMap.put("digit-" + currentDigit, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleInput(String.valueOf(currentDigit));
                }
            });
        }

        inputMap.put(KeyStroke.getKeyStroke("typed ."), "decimal");
        actionMap.put("decimal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput(".");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("typed +"), "plus");
        actionMap.put("plus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput("+");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("typed -"), "minus");
        actionMap.put("minus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput("-");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("typed *"), "multiply");
        actionMap.put("multiply", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput("*");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("typed /"), "divide");
        actionMap.put("divide", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput("/");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("typed %"), "percent");
        actionMap.put("percent", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput("%");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("typed ^"), "power");
        actionMap.put("power", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput("^");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("typed ("), "open");
        actionMap.put("open", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput("(");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke("typed )"), "close");
        actionMap.put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInput(")");
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "equals");
        actionMap.put("equals", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace");
        actionMap.put("backspace", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backspace();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear");
        actionMap.put("clear", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
    }

    private void handleInput(String cmd) {
        switch (cmd) {
            case "C":
                clear();
                break;
            case "←":
                backspace();
                break;
            case "=":
                calculate();
                break;
            case "MC":
                clearMemory();
                break;
            case "MR":
                recallMemory();
                break;
            case "M+":
                addToMemory(true);
                break;
            case "M-":
                addToMemory(false);
                break;
            case "Clear History":
                clearHistory();
                break;
            case "Use Formula":
                applyFormula();
                break;
            case "Apply Formula":
                applyFormula();
                break;
            case "Challenge Me":
                startChallenge();
                break;
            case "Explain":
                applyExplain();
                break;
            case "Graph":
                openGraph();
                break;
            case "🌙":
            case "☀":
                toggleTheme();
                break;
            case "Deg":
            case "Rad":
                useDegrees = !useDegrees;
                if (degRadButton != null) degRadButton.setText(useDegrees ? "Deg" : "Rad");
                updateStatus(useDegrees ? "Using degrees" : "Using radians");
                break;
            case "Inv":
                invMode = !invMode;
                if (invToggleButton != null) invToggleButton.setText(invMode ? "Inv*" : "Inv");
                updateStatus(invMode ? "Inverse trig ON" : "Inverse trig OFF");
                break;
            case "Expand":
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                    setExtendedState(JFrame.NORMAL);
                    updateStatus("Window restored");
                } else {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    updateStatus("Window expanded");
                }
                break;
            case "sin":
                insertText(invMode ? "asin(" : "sin(");
                break;
            case "cos":
                insertText(invMode ? "acos(" : "cos(");
                break;
            case "tan":
                insertText(invMode ? "atan(" : "tan(");
                break;
            case "√":
                insertText("√(");
                break;
            case "log":
                insertText("log(");
                break;
            case "ln":
                insertText("ln(");
                break;
            case "!":
                insertText("!");
                break;
            case "π":
                insertText("π");
                break;
            case "e":
                insertText("e");
                break;
            case "Ans":
                insertText("Ans");
                break;
            case "x²":
                insertText("^2");
                break;
            case ".":
                insertDecimal();
                break;
            default:
                if ("0123456789".contains(cmd)) {
                    insertDigit(cmd);
                } else if ("+-*/^%".contains(cmd)) {
                    insertOperator(cmd);
                } else if (cmd.equals("(") || cmd.equals(")")) {
                    insertParenthesis(cmd);
                }
        }
    }

    private void clear() {
        expression.setLength(0);
        expression.append("0");
        startNewNumber = true;
        display.setText("0");
        resultField.setText("");
        stepsArea.setText("");
        lastExpression = "";
        lastResultText = "";
        updateStatus("Ready to calculate");
    }

    private void clearMemory() {
        memory = 0.0;
        hasMemory = false;
        updateMemoryLabel();
        updateStatus("Memory cleared");
    }

    private void recallMemory() {
        if (hasMemory) {
            expression.setLength(0);
            expression.append(formatNumber(memory));
            startNewNumber = false;
            display.setText(expression.toString());
            updateStatus("Memory recalled");
        }
    }

    private void addToMemory(boolean add) {
        try {
            double value = evaluateExpression(expression.toString()).value;
            memory = add ? memory + value : memory - value;
            hasMemory = true;
            updateMemoryLabel();
            updateStatus("Saved to memory");
        } catch (Exception ignored) {
            updateStatus("Unable to store to memory");
        }
    }

    private void updateMemoryLabel() {
        if (memoryLabel != null) {
            memoryLabel.setText(hasMemory ? "M: " + formatNumber(memory) : "M: 0");
        }
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        applyTheme();
    }

    private void applyTheme() {
        // Purple theme palette
        Color bgPrimary = new Color(18, 24, 38); // #121826
        Color btnColor = new Color(42, 49, 66); // #2A3142
        Color opColor = new Color(124, 77, 255); // #7C4DFF
        Color eqColor = new Color(106, 90, 249); // #6A5AF9
        Color textColor = Color.WHITE;
        Color background = darkMode ? bgPrimary : new Color(242, 248, 255);
        Color foreground = darkMode ? new Color(240, 240, 240) : new Color(28, 34, 44);
        Color controlColor = darkMode ? new Color(44, 54, 71) : new Color(255, 255, 255);
        Color borderColor = darkMode ? new Color(85, 100, 120) : new Color(196, 208, 223);
        Color accentColor = darkMode ? new Color(74, 144, 226) : new Color(67, 120, 197);
        Color glassFill = darkMode ? new Color(35, 43, 58, 200) : new Color(255, 255, 255, 160);
        Color glassBorder = darkMode ? new Color(95, 110, 130, 220) : new Color(220, 230, 242, 220);

        if (rootPanel != null) {
            rootPanel.setGradientColors(
                darkMode ? new Color(20, 22, 36) : new Color(92, 128, 255),
                darkMode ? new Color(26, 18, 46) : new Color(174, 132, 255));
            rootPanel.repaint();
        }
        if (scientificPanel != null) {
            scientificPanel.setBackground(bgPrimary);
            stylePanelButtonsWithPalette(scientificPanel, btnColor, textColor, opColor, eqColor);
        }
        if (keypadPanel != null) {
            keypadPanel.setBackground(bgPrimary);
            stylePanelButtonsWithPalette(keypadPanel, btnColor, textColor, opColor, eqColor);
        }
        if (historyPanel != null) {
            historyPanel.setBackground(background);
            // keep a simple line border + padding; title is rendered separately above
            historyPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
            if (historyPanel instanceof GlassPanel glassPanel) {
                glassPanel.setFillColor(glassFill);
                glassPanel.setBorderColor(glassBorder);
            }
        }
        if (display != null) {
            display.setBackground(darkMode ? new Color(27, 34, 45) : new Color(255, 255, 255));
            display.setForeground(foreground);
            display.setCaretColor(foreground);
            display.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(18, borderColor, 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        }
        if (themeToggleButton != null) {
            themeToggleButton.setText(darkMode ? "☀" : "🌙");
            themeToggleButton.setBackground(btnColor);
            themeToggleButton.setForeground(textColor);
            themeToggleButton.setBorder(new RoundedBorder(16, borderColor, 1));
        }
        if (memoryLabel != null) {
            memoryLabel.setForeground(foreground);
        }
        if (historyListView != null) {
            historyListView.setBackground(new Color(24, 30, 44));
            historyListView.setForeground(textColor);
        }
        if (historyStepsArea != null) {
            historyStepsArea.setBackground(new Color(18, 22, 32));
            historyStepsArea.setForeground(textColor);
        }
        if (formulaArea != null) {
            formulaArea.setBackground(darkMode ? new Color(27, 34, 45) : new Color(255, 255, 255));
            formulaArea.setForeground(foreground);
        }
        if (statusLabel != null) {
            statusLabel.setForeground(foreground);
        }
        if (challengeLabel != null) {
            challengeLabel.setForeground(foreground);
        }
        if (challengeStatusLabel != null) {
            challengeStatusLabel.setForeground(foreground);
        }
        repaint();
    }

    private void stylePanelButtonsWithPalette(JPanel panel, Color btnColor, Color textColor, Color opColor, Color eqColor) {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            Component component = panel.getComponent(i);
            if (component instanceof JButton button) {
                boolean operatorButton = isOperatorButton(button.getText());
                if ("=".equals(button.getText())) {
                    button.setBackground(eqColor);
                } else if (operatorButton) {
                    button.setBackground(opColor);
                } else {
                    button.setBackground(btnColor);
                }
                button.setForeground(textColor);
                button.setBorder(new RoundedBorder(18, btnColor.darker(), 1));
                button.setOpaque(true);
            }
        }
    }

    // Rounded button implementation
    private static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new RoundedBorder(18, new Color(0,0,0,40), 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = getBackground();
            if (bg == null) bg = new Color(42,49,66);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getForeground().darker());
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
            g2.dispose();
        }
    }

    private void stylePanelButtons(JPanel panel, Color controlColor, Color foreground, Color borderColor) {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            Component component = panel.getComponent(i);
            if (component instanceof JButton button) {
                boolean operatorButton = isOperatorButton(button.getText());
                button.setBackground(operatorButton ? controlColor.darker() : controlColor);
                button.setForeground(foreground);
                button.setBorder(new RoundedBorder(16, borderColor, 1));
                button.setOpaque(true);
            }
        }
    }

    private boolean isOperatorButton(String text) {
        return "+-*/^%".contains(text) || "C".equals(text) || "←".equals(text) || "=".equals(text);
    }

    private void backspace() {
        if (expression.length() > 0 && !expression.toString().equals("0")) {
            expression.deleteCharAt(expression.length() - 1);
            if (expression.length() == 0) {
                expression.append("0");
                startNewNumber = true;
            } else {
                startNewNumber = endsWithOperatorOrOpenParen();
            }
            display.setText(expression.toString());
        }
    }

    private void insertDigit(String digit) {
        String current = expression.toString();
        if (startNewNumber) {
            if (current.equals("0") || current.equals("Error") || current.isEmpty()) {
                expression.setLength(0);
                expression.append(digit);
            } else if (endsWithOperatorOrOpenParen()) {
                expression.append(digit);
            } else {
                expression.setLength(0);
                expression.append(digit);
            }
            startNewNumber = false;
        } else {
            expression.append(digit);
        }
        display.setText(expression.toString());
    }

    private void insertOperator(String op) {
        String current = expression.toString();
        if (current.equals("Error")) {
            expression.setLength(0);
            expression.append("0");
        }
        if (current.equals("0") || current.isEmpty()) {
            if ("-".equals(op)) {
                expression.setLength(0);
                expression.append(op);
                startNewNumber = false;
                display.setText(expression.toString());
            }
            return;
        }
        if (endsWithOperatorOrOpenParen()) {
            if ("-".equals(op) && (current.endsWith("(") || current.endsWith("+") || current.endsWith("-") || current.endsWith("*") || current.endsWith("/") || current.endsWith("^") || current.endsWith("%"))) {
                expression.append(op);
            } else {
                expression.deleteCharAt(expression.length() - 1);
                expression.append(op);
            }
        } else {
            expression.append(op);
        }
        startNewNumber = true;
        display.setText(expression.toString());
    }

    private void insertParenthesis(String parenthesis) {
        String current = expression.toString();
        if (current.equals("Error") || current.equals("0") || current.isEmpty()) {
            expression.setLength(0);
            expression.append(parenthesis);
        } else if (parenthesis.equals("(") && !endsWithOperatorOrOpenParen()) {
            expression.append("*");
            expression.append(parenthesis);
        } else {
            expression.append(parenthesis);
        }
        startNewNumber = false;
        display.setText(expression.toString());
    }

    private void insertDecimal() {
        String current = expression.toString();
        if (current.equals("Error") || current.equals("0") || current.isEmpty() || endsWithOperatorOrOpenParen()) {
            expression.setLength(0);
            expression.append("0.");
        } else {
            String currentNumber = getCurrentNumber();
            if (!currentNumber.contains(".")) {
                expression.append(".");
            }
        }
        startNewNumber = false;
        display.setText(expression.toString());
    }

    private void insertText(String text) {
        String current = expression.toString();
        if (current.equals("Error")) {
            expression.setLength(0);
            expression.append("0");
        }
        if ("Ans".equals(text)) {
            if (hasLastResult) {
                if (startNewNumber || current.equals("0") || current.isEmpty()) {
                    expression.setLength(0);
                    expression.append(formatNumber(lastResult));
                } else {
                    expression.append(formatNumber(lastResult));
                }
            } else {
                expression.setLength(0);
                expression.append("0");
            }
        } else {
            if (startNewNumber) {
                expression.setLength(0);
                expression.append(text);
            } else {
                expression.append(text);
            }
        }
        startNewNumber = false;
        display.setText(expression.toString());
    }

    private void calculate() {
        String expr = expression.toString();
        if (expr.equals("0") || expr.equals("Error") || expr.isEmpty()) {
            return;
        }
        try {
            EvaluationResult evaluation = evaluateExpression(expr);
            if (Double.isNaN(evaluation.value) || Double.isInfinite(evaluation.value)) {
                throw new Exception("Invalid result");
            }
            String resultText = formatNumber(evaluation.value);
            display.setText(expression.toString());
            resultField.setText(resultText);
            expression.setLength(0);
            expression.append(resultText);
            lastExpression = expr;
            lastResultText = resultText;
            lastResult = evaluation.value;
            hasLastResult = true;
            startNewNumber = true;
            List<String> visibleSteps = new ArrayList<>(evaluation.steps);
            visibleSteps.add("Final Answer = " + resultText);
            if (stepsArea != null) {
                stepsArea.setText(String.join("\n", visibleSteps));
            }
            appendHistory(expr + " = " + resultText, visibleSteps);
            updateStatus("Calculated " + resultText);
            if (currentChallenge != null && Math.abs(evaluation.value - currentChallenge.answer) < 1e-9) {
                challengeAttempts++;
                challengeScore++;
                updateChallengeStatus("Correct! Score: " + challengeScore + "/" + challengeAttempts, true);
                currentChallenge = null;
            } else if (currentChallenge != null) {
                challengeAttempts++;
                updateChallengeStatus("Not quite. Score: " + challengeScore + "/" + challengeAttempts, false);
                currentChallenge = null;
            }
        } catch (Exception ex) {
            showSmartError(ex.getMessage());
        }
    }

    private EvaluationResult evaluateExpression(String expr) throws Exception {
        return new ExpressionParser(expr, lastResult, Double.NaN, useDegrees).parse();
    }

    private EvaluationResult evaluateExpression(String expr, double xValue) throws Exception {
        return new ExpressionParser(expr, lastResult, xValue, useDegrees).parse();
    }

    private void appendHistory(String entry, List<String> steps) {
        HistoryEntry he = new HistoryEntry(entry, steps);
        historyList.add(0, he);
        if (historyList.size() > 20) {
            historyList.remove(historyList.size() - 1);
        }
        refreshHistoryDisplay();
        saveHistoryToFile();
    }

    private static File resolveHistoryFile() {
        File userHome = new File(System.getProperty("user.home"));
        File oneDrive = new File(userHome, "OneDrive");
        File desktop = new File(oneDrive, "Desktop");
        File desktopFile = new File(desktop, "calculator_history.txt");
        if (desktopFile.exists()) {
            return desktopFile;
        }
        File localFile = new File("calculator_history.txt");
        return localFile;
    }

    private void loadHistoryFromFile() {
        historyList.clear();
        if (!historyFile.exists()) {
            refreshHistoryDisplay();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
            String line;
            String currentHeader = null;
            List<String> currentSteps = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ENTRY|")) {
                    if (currentHeader != null) {
                        historyList.add(new HistoryEntry(currentHeader, currentSteps));
                    }
                    currentHeader = line.substring(6);
                    currentSteps = new ArrayList<>();
                } else if (line.startsWith("STEP|")) {
                    currentSteps.add(line.substring(5));
                }
            }
            if (currentHeader != null) {
                historyList.add(new HistoryEntry(currentHeader, currentSteps));
            }
        } catch (IOException ex) {
            updateStatus("Unable to load saved history");
        }
        refreshHistoryDisplay();
    }

    private void saveHistoryToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile))) {
            for (HistoryEntry entry : historyList) {
                writer.write("ENTRY|" + entry.header);
                writer.newLine();
                for (String step : entry.steps) {
                    writer.write("STEP|" + step);
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            updateStatus("Unable to save history");
        }
    }

    private void clearHistory() {
        historyList.clear();
        if (historyListModel != null) {
            historyListModel.clear();
        }
        if (historyStepsArea != null) {
            historyStepsArea.setText("");
        }
        saveHistoryToFile();
        updateStatus("History cleared");
    }

    private void refreshHistoryDisplay() {
        if (historyListModel == null) return;
        historyListModel.clear();
        for (HistoryEntry he : historyList) {
            historyListModel.addElement(he);
        }
        if (!historyList.isEmpty()) {
            historyListView.setSelectedIndex(0);
            historyListView.ensureIndexIsVisible(0);
        } else if (historyStepsArea != null) {
            historyStepsArea.setText("No history yet. Perform a calculation to store steps.");
        }
        updateHistoryDetail();
        if (historyListView != null) {
            historyListView.revalidate();
            historyListView.repaint();
        }
    }

    private void updateHistoryDetail() {
        HistoryEntry he = historyListView.getSelectedValue();
        if (he == null) {
            if (historyStepsArea != null) {
                historyStepsArea.setText("Select a history entry to view its calculation steps.");
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Calculation:\n");
        sb.append(he.header).append("\n\n");
        sb.append("Steps:\n");
        if (he.steps.isEmpty()) {
            sb.append("No intermediate steps available.\n");
        } else {
            for (String step : he.steps) {
                sb.append("• ").append(step).append("\n");
            }
        }
        sb.append("\nFinal answer: ").append(he.header.substring(he.header.lastIndexOf(" = ") + 3));
        if (historyStepsArea != null) {
            historyStepsArea.setText(sb.toString());
            historyStepsArea.setCaretPosition(0);
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void showSmartError(String message) {
        String friendly = "Unable to calculate";
        if (message != null) {
            if (message.contains("Division by zero") || message.contains("divide by zero")) {
                friendly = "Cannot divide by zero.";
            } else if (message.contains("Missing closing parenthesis") || message.contains("parenthesis")) {
                friendly = "Check your brackets.";
            } else if (message.contains("Unexpected character") || message.contains("Unknown identifier")) {
                friendly = "Unsupported input.";
            } else if (message.contains("Invalid result")) {
                friendly = "The result is not valid.";
            } else {
                friendly = message;
            }
        }
        display.setText(friendly);
        expression.setLength(0);
        expression.append("0");
        startNewNumber = true;
        hasLastResult = false;
        updateStatus(friendly);
    }

    private void applyExplain() {
        String expr = expression.toString();
        if (expr != null && !expr.isBlank() && expr.equals(lastResultText) && !lastExpression.isBlank()) {
            expr = lastExpression;
        }
        if (expr == null || expr.isBlank() || "0".equals(expr)) {
            updateStatus("Nothing to explain");
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (expr.contains("%")) {
            sb.append("Percent explanation:\n");
            sb.append("15% of 200 => (15/100) × 200 = 30\n");
        } else if (expr.matches(".*\\^.*")) {
            sb.append("Power explanation:\n");
            sb.append("a ^ b means a multiplied by itself b times.\n");
        } else if (expr.matches(".*sqrt.*|.*√.*")) {
            sb.append("Square root explanation:\n");
            sb.append("sqrt(x) = a means a² = x.\n");
        } else {
            sb.append("Step-by-step:\n");
            try {
                EvaluationResult res = evaluateExpression(expr);
                for (String s : res.steps) sb.append(s).append('\n');
                sb.append("Final: ").append(formatNumber(res.value)).append('\n');
            } catch (Exception ex) {
                sb.append("Unable to explain: ").append(ex.getMessage()).append('\n');
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Explain", JOptionPane.INFORMATION_MESSAGE);
        if (stepsArea != null) {
            stepsArea.setText(sb.toString());
        }
    }

    private void openGraph() {
        String expr = expression.toString();
        if (expr == null || expr.isBlank()) {
            updateStatus("No expression to graph");
            return;
        }
        GraphSettings settings = promptGraphSettings(expr);
        if (settings == null) {
            updateStatus("Graph cancelled");
            return;
        }
        JFrame gf = new JFrame("Graph - " + expr);
        gf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gf.setSize(820, 520);
        gf.setLocationRelativeTo(this);
        gf.add(new GraphPanel(expr, useDegrees, settings.minX, settings.maxX, settings.samples));
        gf.setVisible(true);
    }

    private GraphSettings promptGraphSettings(String expr) {
        JTextField minXField = new JTextField("-10");
        JTextField maxXField = new JTextField("10");
        JTextField samplesField = new JTextField("700");
        JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("X minimum:"));
        panel.add(minXField);
        panel.add(new JLabel("X maximum:"));
        panel.add(maxXField);
        panel.add(new JLabel("Plot points (100-2000):"));
        panel.add(samplesField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Graph settings for " + expr,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        try {
            double minX = Double.parseDouble(minXField.getText().trim());
            double maxX = Double.parseDouble(maxXField.getText().trim());
            int samples = Integer.parseInt(samplesField.getText().trim());
            if (minX >= maxX) {
                JOptionPane.showMessageDialog(this, "X minimum must be less than X maximum.", "Invalid range", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (samples < 100 || samples > 2000) {
                JOptionPane.showMessageDialog(this, "Plot points should be between 100 and 2000.", "Invalid value", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return new GraphSettings(minX, maxX, samples);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric settings.", "Invalid input", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private static class GraphSettings {
        final double minX;
        final double maxX;
        final int samples;

        GraphSettings(double minX, double maxX, int samples) {
            this.minX = minX;
            this.maxX = maxX;
            this.samples = samples;
        }
    }

    private class GraphPanel extends JPanel {
        private final String expr;
        private final boolean useDegrees;
        private final double minX;
        private final double maxX;
        private final int samples;
        private final List<Point> points;

        GraphPanel(String expr, boolean useDegrees, double minX, double maxX, int samples) {
            this.expr = expr;
            this.useDegrees = useDegrees;
            this.minX = minX;
            this.maxX = maxX;
            this.samples = samples;
            this.points = new ArrayList<>();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int margin = 40;
            g2.setColor(new Color(24, 26, 40));
            g2.fillRect(0, 0, w, h);

            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            points.clear();
            int plotWidth = w - margin * 2;
            for (int i = 0; i <= samples; i++) {
                double x = minX + (maxX - minX) * i / Math.max(1, samples);
                double y;
                try {
                    y = evaluateGraphExpression(expr, x);
                } catch (Exception ex) {
                    y = Double.NaN;
                }
                if (Double.isFinite(y)) {
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
                int px = margin + (int) Math.round((x - minX) / (maxX - minX) * plotWidth);
                points.add(new Point(px, Double.isFinite(y) ? (int) Math.round(y) : Integer.MIN_VALUE));
            }
            if (minY == Double.POSITIVE_INFINITY || maxY == Double.NEGATIVE_INFINITY) {
                g2.setColor(Color.RED);
                g2.drawString("Unable to plot expression in the chosen range.", margin, h / 2);
                g2.dispose();
                return;
            }
            if (Math.abs(maxY - minY) < 1e-6) {
                maxY = minY + 1;
                minY = minY - 1;
            }
            double rangeY = maxY - minY;
            double scaleX = plotWidth / (maxX - minX);
            double scaleY = (h - margin * 2) / rangeY;
            int xAxisY = margin + (int) Math.round((maxY) * scaleY);
            int yAxisX = margin + (int) Math.round((-minX) / (maxX - minX) * plotWidth);

            g2.setColor(new Color(80, 90, 110));
            for (double xTick = Math.ceil(minX); xTick <= maxX; xTick++) {
                int px = margin + (int) Math.round((xTick - minX) * scaleX);
                g2.drawLine(px, margin, px, h - margin);
            }
            for (double yTick = Math.ceil(minY); yTick <= maxY; yTick++) {
                int py = margin + (int) Math.round((maxY - yTick) * scaleY);
                g2.drawLine(margin, py, w - margin, py);
            }
            g2.setColor(new Color(200, 200, 220));
            g2.setStroke(new java.awt.BasicStroke(2f));
            if (minY <= 0 && maxY >= 0) {
                int zeroY = margin + (int) Math.round((maxY - 0) * scaleY);
                g2.drawLine(margin, zeroY, w - margin, zeroY);
            }
            if (minX <= 0 && maxX >= 0) {
                int zeroX = margin + (int) Math.round((0 - minX) * scaleX);
                g2.drawLine(zeroX, margin, zeroX, h - margin);
            }

            g2.setColor(new Color(146, 120, 255));
            g2.setStroke(new java.awt.BasicStroke(2f));
            Point prev = null;
            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);
                if (p.y == Integer.MIN_VALUE) {
                    prev = null;
                    continue;
                }
                double x = minX + (maxX - minX) * i / Math.max(1, samples);
                double y = (double) p.y;
                int py = margin + (int) Math.round((maxY - y) * scaleY);
                if (prev != null && Math.abs(prev.y - py) < h) {
                    g2.drawLine(prev.x, prev.y, p.x, py);
                }
                prev = new Point(p.x, py);
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("y = " + expr, margin + 2, margin - 12);
            g2.drawString("X range: [" + formatDouble(minX) + ", " + formatDouble(maxX) + "]", margin + 2, h - margin + 16);
            g2.drawString("Y range: [" + formatDouble(minY) + ", " + formatDouble(maxY) + "]", margin + 220, h - margin + 16);
            g2.drawString(useDegrees ? "Angle mode: Deg" : "Angle mode: Rad", margin + 430, h - margin + 16);
            g2.dispose();
        }

        private double evaluateGraphExpression(String expr, double xVal) throws Exception {
            return new ExpressionParser(expr, 0, xVal, useDegrees).parse().value;
        }

        private String formatDouble(double value) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return "?";
            }
            if (Math.abs(value - Math.round(value)) < 1e-8) {
                return String.valueOf((long) Math.round(value));
            }
            return String.format(Locale.US, "%.4f", value);
        }
    }

    private void applyFormula() {
        String selection = formulaFieldsCombo == null || formulaFieldsCombo.getSelectedItem() == null ? "" : (String) formulaFieldsCombo.getSelectedItem();
        if (selection == null || selection.isBlank()) {
            updateStatus("Select a formula first");
            return;
        }
        StringBuilder details = new StringBuilder();
        try {
            switch (selection) {
                case "Circle" -> {
                    double radius = readDouble("Circle radius");
                    double area = Math.PI * radius * radius;
                    details.append("Formula: A = πr²\n");
                    details.append("Calculation: A = π × ").append(formatNumber(radius)).append("²\n");
                    details.append("Result: ").append(formatNumber(area));
                    expression.setLength(0);
                    expression.append(formatNumber(area));
                    display.setText(formatNumber(area));
                    startNewNumber = true;
                }
                case "Triangle" -> {
                    double base = readDouble("Base");
                    double height = readDouble("Height");
                    double area = 0.5 * base * height;
                    details.append("Formula: A = ½bh\n");
                    details.append("Calculation: A = 0.5 × ").append(formatNumber(base)).append(" × ").append(formatNumber(height)).append("\n");
                    details.append("Result: ").append(formatNumber(area));
                    expression.setLength(0);
                    expression.append(formatNumber(area));
                    display.setText(formatNumber(area));
                    startNewNumber = true;
                }
                case "Rectangle" -> {
                    double length = readDouble("Length");
                    double width = readDouble("Width");
                    double area = length * width;
                    details.append("Formula: A = l × w\n");
                    details.append("Calculation: A = ").append(formatNumber(length)).append(" × ").append(formatNumber(width)).append("\n");
                    details.append("Result: ").append(formatNumber(area));
                    expression.setLength(0);
                    expression.append(formatNumber(area));
                    display.setText(formatNumber(area));
                    startNewNumber = true;
                }
                case "Pythagoras" -> {
                    double a = readDouble("Side A");
                    double b = readDouble("Side B");
                    double c = Math.sqrt(a * a + b * b);
                    details.append("Formula: c = √(a² + b²)\n");
                    details.append("Calculation: c = √(").append(formatNumber(a)).append("² + ").append(formatNumber(b)).append("²)\n");
                    details.append("Result: ").append(formatNumber(c));
                    expression.setLength(0);
                    expression.append(formatNumber(c));
                    display.setText(formatNumber(c));
                    startNewNumber = true;
                }
                case "Quadratic equation" -> {
                    double a = readDouble("Coefficient a");
                    double b = readDouble("Coefficient b");
                    double c = readDouble("Coefficient c");
                    double discriminant = b * b - 4 * a * c;
                    if (discriminant < 0) {
                        throw new Exception("Discriminant is negative");
                    }
                    double root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
                    double root2 = (-b - Math.sqrt(discriminant)) / (2 * a);
                    details.append("Formula: x = (-b ± √(b² - 4ac)) / 2a\n");
                    details.append("Roots: ").append(formatNumber(root1)).append(" and ").append(formatNumber(root2));
                    expression.setLength(0);
                    expression.append(formatNumber(root1));
                    display.setText(formatNumber(root1));
                    startNewNumber = true;
                }
                case "Simple Interest" -> {
                    double principal = readDouble("Principal");
                    double rate = readDouble("Rate (%)");
                    double time = readDouble("Time (years)");
                    double interest = principal * rate * time / 100.0;
                    details.append("Formula: SI = P × R × T / 100\n");
                    details.append("Result: ").append(formatNumber(interest));
                    expression.setLength(0);
                    expression.append(formatNumber(interest));
                    display.setText(formatNumber(interest));
                    startNewNumber = true;
                }
                case "Compound Interest" -> {
                    double principal = readDouble("Principal");
                    double rate = readDouble("Rate (%)");
                    double time = readDouble("Time (years)");
                    double frequency = readDouble("Compounds per year");
                    double amount = principal * Math.pow(1 + rate / (100.0 * frequency), frequency * time);
                    details.append("Formula: A = P(1 + r/n)^(nt)\n");
                    details.append("Result: ").append(formatNumber(amount));
                    expression.setLength(0);
                    expression.append(formatNumber(amount));
                    display.setText(formatNumber(amount));
                    startNewNumber = true;
                }
                case "Cylinder" -> {
                    double radius = readDouble("Radius");
                    double height = readDouble("Height");
                    double volume = Math.PI * radius * radius * height;
                    details.append("Formula: V = πr²h\n");
                    details.append("Result: ").append(formatNumber(volume));
                    expression.setLength(0);
                    expression.append(formatNumber(volume));
                    display.setText(formatNumber(volume));
                    startNewNumber = true;
                }
                case "Sphere" -> {
                    double radius = readDouble("Radius");
                    double volume = 4.0 / 3.0 * Math.PI * radius * radius * radius;
                    details.append("Formula: V = 4/3 πr³\n");
                    details.append("Result: ").append(formatNumber(volume));
                    expression.setLength(0);
                    expression.append(formatNumber(volume));
                    display.setText(formatNumber(volume));
                    startNewNumber = true;
                }
                case "Force" -> {
                    double mass = readDouble("Mass");
                    double acceleration = readDouble("Acceleration");
                    double force = mass * acceleration;
                    details.append("Formula: F = m × a\n");
                    details.append("Result: ").append(formatNumber(force));
                    expression.setLength(0);
                    expression.append(formatNumber(force));
                    display.setText(formatNumber(force));
                    startNewNumber = true;
                }
                case "Velocity" -> {
                    double distance = readDouble("Distance");
                    double time = readDouble("Time");
                    double velocity = distance / time;
                    details.append("Formula: v = d / t\n");
                    details.append("Result: ").append(formatNumber(velocity));
                    expression.setLength(0);
                    expression.append(formatNumber(velocity));
                    display.setText(formatNumber(velocity));
                    startNewNumber = true;
                }
                case "Energy" -> {
                    double mass = readDouble("Mass");
                    double c = 299792458;
                    double energy = mass * c * c;
                    details.append("Formula: E = mc²\n");
                    details.append("Result: ").append(formatNumber(energy));
                    expression.setLength(0);
                    expression.append(formatNumber(energy));
                    display.setText(formatNumber(energy));
                    startNewNumber = true;
                }
                case "Mean" -> {
                    details.append("Compute mean using the expression interface.\n");
                    details.append("Enter values separated by + and then divide by the count.\n");
                    expression.setLength(0);
                    expression.append("0");
                    display.setText("0");
                    startNewNumber = true;
                }
                case "Standard Deviation" -> {
                    details.append("Standard deviation is supported through calculation expressions.\n");
                    details.append("Use the formula panel to select inputs and solve manually.");
                    expression.setLength(0);
                    expression.append("0");
                    display.setText("0");
                    startNewNumber = true;
                }
                case "Probability" -> {
                    details.append("Probability formulas can be computed as p = favorable / total.\n");
                    expression.setLength(0);
                    expression.append("0");
                    display.setText("0");
                    startNewNumber = true;
                }
                default -> updateStatus("Formula assistant is ready");
            }
            formulaArea.setText(details.toString());
            updateStatus("Formula added to the calculator");
        } catch (Exception ex) {
            formulaArea.setText("Unable to compute with the selected formula.\n" + ex.getMessage());
            updateStatus("Formula assistant error");
        }
    }

    private double readDouble(String label) {
        String input = JOptionPane.showInputDialog(this, label + ":", "Formula Assistant", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("No value entered");
        }
        return Double.parseDouble(input);
    }

    private void startChallenge() {
        currentChallenge = generateChallenge();
        expression.setLength(0);
        expression.append(currentChallenge.expression);
        display.setText("Q: " + currentChallenge.displayText);
        startNewNumber = false;
        updateChallengeStatus("Solve it and press =", true);
        updateStatus("Challenge started");
    }

    private void updateChallengeStatus(String text, boolean positive) {
        if (challengeLabel != null) {
            challengeLabel.setText(text);
            challengeLabel.setForeground(positive ? new Color(0, 128, 0) : new Color(180, 60, 60));
        }
        if (challengeStatusLabel != null) {
            challengeStatusLabel.setText("Score: " + challengeScore + "/" + challengeAttempts);
        }
    }

    private Challenge generateChallenge() {
        int choice = random.nextInt(4);
        return switch (choice) {
            case 0 -> {
                int value = random.nextInt(10) + 2;
                int offset = random.nextInt(20) + 1;
                yield new Challenge("sqrt(" + (value * value) + ") + " + offset, "√" + (value * value) + " + " + offset, (double) value + offset);
            }
            case 1 -> {
                int value = random.nextInt(9) + 2;
                int offset = random.nextInt(10) + 1;
                yield new Challenge("" + value + "^2 + " + offset, value + "² + " + offset, Math.pow(value, 2) + offset);
            }
            case 2 -> {
                int a = random.nextInt(8) + 2;
                int b = random.nextInt(8) + 2;
                yield new Challenge(a + " + " + b + " * 2", a + " + " + b + " × 2", a + b * 2.0);
            }
            default -> {
                int a = random.nextInt(6) + 2;
                int b = random.nextInt(6) + 2;
                yield new Challenge("(" + a + " + " + b + ") * 3", "(" + a + " + " + b + ") × 3", (a + b) * 3.0);
            }
        };
    }

    private String getCurrentNumber() {
        int end = expression.length();
        int idx = end - 1;
        while (idx >= 0 && (Character.isDigit(expression.charAt(idx)) || expression.charAt(idx) == '.')) {
            idx--;
        }
        return expression.substring(idx + 1);
    }

    private boolean endsWithOperatorOrOpenParen() {
        String current = expression.toString();
        return current.endsWith("+") || current.endsWith("-") || current.endsWith("*") || current.endsWith("/") || current.endsWith("^") || current.endsWith("%") || current.endsWith("(");
    }

    private static String formatNumber(double value) {
        if (Math.abs(value) < 1e-12) {
            value = 0.0;
        }
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.format(Locale.US, "%.10g", value);
    }

    private static class ExpressionParser {
        private final String expression;
        private final double lastResult;
        private final double xValue;
        private final boolean useDegrees;
        private int index = 0;
        private final List<Token> tokens;
        private final List<String> steps = new ArrayList<>();
        private int stepCounter = 0;

        ExpressionParser(String expression, double lastResult, double xValue, boolean useDegrees) throws Exception {
            this.expression = expression;
            this.lastResult = lastResult;
            this.xValue = xValue;
            this.useDegrees = useDegrees;
            this.tokens = tokenize(expression);
        }

        ExpressionParser(String expression, double lastResult, boolean useDegrees) throws Exception {
            this(expression, lastResult, Double.NaN, useDegrees);
        }

        EvaluationResult parse() throws Exception {
            double value = parseAddition();
            if (peek().type != TokenType.EOF) {
                throw new Exception("Unexpected token");
            }
            return new EvaluationResult(value, steps);
        }

        private double parseAddition() throws Exception {
            double value = parseMultiplication();
            while (match("+", "-")) {
                String op = previous().value;
                double right = parseMultiplication();
                double next = "+".equals(op) ? value + right : value - right;
                recordStep(formatNumber(value), op, formatNumber(right), next);
                value = next;
            }
            return value;
        }

        private double parseMultiplication() throws Exception {
            double value = parseUnary();
            while (match("*", "/", "%")) {
                String op = previous().value;
                double right = parseUnary();
                double left = value;
                double result;
                if ("*".equals(op)) {
                    result = left * right;
                } else if ("/".equals(op)) {
                    if (right == 0.0) {
                        throw new Exception("Cannot divide by zero.");
                    }
                    result = left / right;
                } else {
                    result = left % right;
                }
                recordStep(formatNumber(left), op, formatNumber(right), result);
                value = result;
            }
            return value;
        }

        private double parseUnary() throws Exception {
            if (match("+")) {
                return parseUnary();
            }
            if (match("-")) {
                return -parseUnary();
            }
            return parsePower();
        }

        private double parsePower() throws Exception {
            double value = parseFactorial();
            if (match("^")) {
                double exponent = parsePower();
                double result = Math.pow(value, exponent);
                recordStep(formatNumber(value), "^", formatNumber(exponent), result);
                return result;
            }
            return value;
        }

        private double parseFactorial() throws Exception {
            double value = parsePrimary();
            while (match("!")) {
                if (value < 0 || value != (int) value) {
                    throw new Exception("Factorial requires non-negative integer");
                }
                int n = (int) value;
                double result = factorial(n);
                recordFunctionStep(formatNumber(n) + "!", result);
                value = result;
            }
            return value;
        }

        private double factorial(int n) {
            if (n > 170) return Double.POSITIVE_INFINITY;
            double res = 1;
            for (int i = 2; i <= n; i++) res *= i;
            return res;
        }

        private double parsePrimary() throws Exception {
            Token token = peek();
            if (token.type == TokenType.NUMBER) {
                index++;
                return token.number;
            }
            if (token.type == TokenType.IDENT) {
                String name = advance().value;
                if ("ans".equals(name)) {
                    return lastResult;
                }
                if ("π".equals(name) || "pi".equals(name)) {
                    return Math.PI;
                }
                if ("e".equals(name)) {
                    return Math.E;
                }
                if ("x".equals(name)) {
                    return xValue;
                }
                if ("sin".equals(name) || "cos".equals(name) || "tan".equals(name)
                        || "asin".equals(name) || "acos".equals(name) || "atan".equals(name)
                        || "sqrt".equals(name) || "√".equals(name) || "log".equals(name) || "ln".equals(name)) {
                    int startIdx = steps.size();
                    double argument = parseFunctionArgument();
                    // indent any newly added steps from argument parsing
                    for (int i = startIdx; i < steps.size(); i++) {
                        steps.set(i, "  " + steps.get(i));
                    }
                    steps.add(startIdx, "Evaluate argument for " + name + ":");
                    double result;
                    switch (name) {
                        case "sin":
                            result = Math.sin(useDegrees ? Math.toRadians(argument) : argument);
                            break;
                        case "cos":
                            result = Math.cos(useDegrees ? Math.toRadians(argument) : argument);
                            break;
                        case "tan":
                            result = Math.tan(useDegrees ? Math.toRadians(argument) : argument);
                            break;
                        case "asin":
                            result = Math.asin(argument);
                            if (useDegrees) result = Math.toDegrees(result);
                            break;
                        case "acos":
                            result = Math.acos(argument);
                            if (useDegrees) result = Math.toDegrees(result);
                            break;
                        case "atan":
                            result = Math.atan(argument);
                            if (useDegrees) result = Math.toDegrees(result);
                            break;
                        case "sqrt":
                            result = Math.sqrt(argument);
                            break;
                        case "log":
                            result = Math.log10(argument);
                            break;
                        case "ln":
                            result = Math.log(argument);
                            break;
                        default:
                            throw new Exception("Unsupported function");
                    }
                    recordFunctionStep(name + "(" + formatNumber(argument) + ")", result);
                    return result;
                }
                throw new Exception("Unknown identifier: " + name);
            }
            if (match("(")) {
                int startIdx = steps.size();
                double value = parseAddition();
                if (!match(")")) {
                    throw new Exception("Missing closing parenthesis");
                }
                for (int i = startIdx; i < steps.size(); i++) {
                    steps.set(i, "  " + steps.get(i));
                }
                steps.add(startIdx, "Evaluate parentheses:");
                return value;
            }
            throw new Exception("Unexpected token");
        }

        private double parseFunctionArgument() throws Exception {
            if (match("(")) {
                double value = parseAddition();
                if (!match(")")) {
                    throw new Exception("Missing closing parenthesis");
                }
                return value;
            }
            return parseUnary();
        }

        private void recordStep(String left, String op, String right, double result) {
            stepCounter++;
            steps.add("Step " + stepCounter + ": " + left + " " + op + " " + right + " = " + formatNumber(result));
        }

        private void recordFunctionStep(String expression, double result) {
            stepCounter++;
            steps.add("Step " + stepCounter + ": " + expression + " = " + formatNumber(result));
        }

        

        private Token peek() {
            if (index < tokens.size()) {
                return tokens.get(index);
            }
            return new Token(TokenType.EOF, "");
        }

        private Token previous() {
            return tokens.get(index - 1);
        }

        private Token advance() {
            Token token = peek();
            index++;
            return token;
        }

        private boolean match(String... values) {
            Token token = peek();
            for (String value : values) {
                if (token.value.equals(value)) {
                    index++;
                    return true;
                }
            }
            return false;
        }

        private List<Token> tokenize(String input) throws Exception {
            List<Token> result = new ArrayList<>();
            int i = 0;
            while (i < input.length()) {
                char ch = input.charAt(i);
                if (Character.isWhitespace(ch)) {
                    i++;
                    continue;
                }
                if (Character.isDigit(ch) || ch == '.') {
                    int start = i;
                    boolean seenDot = false;
                    while (i < input.length()) {
                        char current = input.charAt(i);
                        if (current == '.') {
                            if (seenDot) {
                                break;
                            }
                            seenDot = true;
                            i++;
                            continue;
                        }
                        if (Character.isDigit(current)) {
                            i++;
                            continue;
                        }
                        break;
                    }
                    String numberText = input.substring(start, i);
                    result.add(new Token(TokenType.NUMBER, numberText, Double.parseDouble(numberText)));
                    continue;
                }
                if (Character.isLetter(ch) || ch == 'π' || ch == '√') {
                    int start = i;
                    while (i < input.length() && (Character.isLetter(input.charAt(i)) || input.charAt(i) == 'π' || input.charAt(i) == '√')) {
                        i++;
                    }
                    String ident = input.substring(start, i).toLowerCase(Locale.US);
                    if ("√".equals(ident)) {
                        ident = "sqrt";
                    }
                    result.add(new Token(TokenType.IDENT, ident));
                    continue;
                }
                if ("+-*/^%()!".indexOf(ch) >= 0) {
                    result.add(new Token(TokenType.OPERATOR, String.valueOf(ch)));
                    i++;
                    continue;
                }
                throw new Exception("Unexpected character: " + ch);
            }
            result.add(new Token(TokenType.EOF, ""));
            return result;
        }
    }

    private static class Token {
        private final TokenType type;
        private final String value;
        private final double number;

        Token(TokenType type, String value) {
            this(type, value, 0.0);
        }

        Token(TokenType type, String value, double number) {
            this.type = type;
            this.value = value;
            this.number = number;
        }
    }

    private enum TokenType {
        NUMBER,
        IDENT,
        OPERATOR,
        EOF
    }

    public static class EvaluationResult {
        public final double value;
        public final List<String> steps;

        public EvaluationResult(double value, List<String> steps) {
            this.value = value;
            this.steps = steps;
        }
    }

    // Public test entrypoint for parser evaluation
    public static EvaluationResult evaluateForTest(String expr) {
        try {
            return new ExpressionParser(expr, 0, true).parse();
        } catch (Exception e) {
            List<String> err = new ArrayList<>();
            err.add("Error: " + e.getMessage());
            return new EvaluationResult(Double.NaN, err);
        }
    }

    private static class Challenge {
        private final String expression;
        private final String displayText;
        private final double answer;

        private Challenge(String expression, String displayText, double answer) {
            this.expression = expression;
            this.displayText = displayText;
            this.answer = answer;
        }
    }

    private static class HistoryEntry {
        private final String header;
        private final List<String> steps;

        HistoryEntry(String header, List<String> steps) {
            this.header = header;
            this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
        }
    }

    private String toHtmlPreview(String text, int widthPx) {
        if (text == null) return "";
        String escaped = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
        // Limit width in CSS so JLabel wraps the text
        int w = Math.max(180, Math.min(420, widthPx));
        return "<html><div style='width:" + w + "px; font-family:Segoe UI; font-size:12px;'>" + escaped + "</div></html>";
    }

    private static class GradientPanel extends JPanel {
        private Color topColor = new Color(92, 128, 255);
        private Color bottomColor = new Color(174, 132, 255);

        GradientPanel() {
            setOpaque(false);
        }

        void setGradientColors(Color topColor, Color bottomColor) {
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
            g2.setPaint(gradient);
            g2.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 28, 28));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GlassPanel extends JPanel {
        private Color fillColor;
        private Color borderColor;
        private final int cornerRadius;

        GlassPanel(Color fillColor, Color borderColor, int cornerRadius) {
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        void setFillColor(Color fillColor) {
            this.fillColor = fillColor;
            repaint();
        }

        void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(fillColor);
            g2.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius));
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;
        private final int thickness;

        RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x + 1, y + 1, width - 2, height - 2, radius, radius));
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}