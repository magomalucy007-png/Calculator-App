import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;

public class Calculator extends JFrame implements ActionListener {
    private final JTextField display;
    private final StringBuilder expression = new StringBuilder("0");
    private boolean startNewNumber = true;
    private double lastResult = 0.0;
    private boolean hasLastResult = false;

    private final String[] buttonLabels = {
        "7", "8", "9", "/", "C", "sin",
        "4", "5", "6", "*", "(", ")",
        "1", "2", "3", "-", "cos", "tan",
        "0", ".", "=", "+", "sqrt", "log",
        "π", "e", "^", "Ans", "ln", "x²"
    };

    public Calculator() {
        super("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6, 6));

        display = new JTextField("0");
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setFont(new Font("Segoe UI", Font.BOLD, 24));
        display.setBackground(new Color(245, 248, 250));
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 6, 6, 6));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Segoe UI", Font.BOLD, 16));
            button.addActionListener(this);
            button.setFocusable(false);
            button.setBackground(new Color(250, 250, 250));
            buttonPanel.add(button);
        }
        add(buttonPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                display.requestFocusInWindow();
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = ((JButton) e.getSource()).getText();
        handleInput(cmd);
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
            case "sin":
                insertText("sin(");
                break;
            case "cos":
                insertText("cos(");
                break;
            case "tan":
                insertText("tan(");
                break;
            case "sqrt":
                insertText("sqrt(");
                break;
            case "log":
                insertText("log(");
                break;
            case "ln":
                insertText("ln(");
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
                } else if ("+-*/^".contains(cmd)) {
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
            double result = evaluateExpression(expr);
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new Exception("Invalid result");
            }
            String resultText = formatNumber(result);
            display.setText(resultText);
            expression.setLength(0);
            expression.append(resultText);
            lastResult = result;
            hasLastResult = true;
            startNewNumber = true;
        } catch (Exception ex) {
            display.setText("Error");
            expression.setLength(0);
            expression.append("0");
            startNewNumber = true;
            hasLastResult = false;
        }
    }

    private double evaluateExpression(String expr) throws Exception {
        return new ExpressionParser(expr, lastResult).parse();
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

    private String formatNumber(double value) {
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
        private int index = 0;
        private final List<Token> tokens;

        ExpressionParser(String expression, double lastResult) throws Exception {
            this.expression = expression;
            this.lastResult = lastResult;
            this.tokens = tokenize(expression);
        }

        double parse() throws Exception {
            double value = parseAddition();
            if (peek().type != TokenType.EOF) {
                throw new Exception("Unexpected token");
            }
            return value;
        }

        private double parseAddition() throws Exception {
            double value = parseMultiplication();
            while (match("+", "-")) {
                String op = previous().value;
                double right = parseMultiplication();
                value = "+".equals(op) ? value + right : value - right;
            }
            return value;
        }

        private double parseMultiplication() throws Exception {
            double value = parseUnary();
            while (match("*", "/", "%")) {
                String op = previous().value;
                double right = parseUnary();
                if ("*".equals(op)) {
                    value *= right;
                } else if ("/".equals(op)) {
                    if (right == 0.0) {
                        throw new Exception("Division by zero");
                    }
                    value /= right;
                } else {
                    value %= right;
                }
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
            double value = parsePrimary();
            if (match("^")) {
                double exponent = parsePower();
                return Math.pow(value, exponent);
            }
            return value;
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
                if ("sin".equals(name) || "cos".equals(name) || "tan".equals(name) || "sqrt".equals(name) || "log".equals(name) || "ln".equals(name)) {
                    double argument = parseFunctionArgument();
                    switch (name) {
                        case "sin":
                            return Math.sin(Math.toRadians(argument));
                        case "cos":
                            return Math.cos(Math.toRadians(argument));
                        case "tan":
                            return Math.tan(Math.toRadians(argument));
                        case "sqrt":
                            return Math.sqrt(argument);
                        case "log":
                            return Math.log10(argument);
                        case "ln":
                            return Math.log(argument);
                        default:
                            throw new Exception("Unsupported function");
                    }
                }
                throw new Exception("Unknown identifier: " + name);
            }
            if (match("(")) {
                double value = parseAddition();
                if (!match(")")) {
                    throw new Exception("Missing closing parenthesis");
                }
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
            return tokens.get(index++);
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
                if (Character.isLetter(ch) || ch == 'π') {
                    int start = i;
                    while (i < input.length() && (Character.isLetter(input.charAt(i)) || input.charAt(i) == 'π')) {
                        i++;
                    }
                    String ident = input.substring(start, i).toLowerCase(Locale.US);
                    result.add(new Token(TokenType.IDENT, ident));
                    continue;
                }
                if ("+-*/^%()".indexOf(ch) >= 0) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}