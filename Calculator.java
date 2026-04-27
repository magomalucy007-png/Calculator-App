import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Calculator extends JFrame implements ActionListener, KeyListener {
    private JTextField display;
    private StringBuilder expression = new StringBuilder("0");
    private boolean startNewNumber = true;
    private final String buttons[] = {
        "7", "8", "9", "/", "C",
        "4", "5", "6", "*", "←",
        "1", "2", "3", "-", "(",
        "0", ".", "=", "+", ")",
        "±"
    };

    public Calculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLayout(new BorderLayout());

        display = new JTextField("0");
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 28));
        add(display, BorderLayout.NORTH);
        display.addKeyListener(this);

        JPanel panel = new JPanel(new GridLayout(5, 5, 5, 5));
        for (String txt : buttons) {
            JButton btn = new JButton(txt);
            btn.setFont(new Font("Arial", Font.BOLD, 22));
            btn.addActionListener(this);
            btn.setFocusable(false);
            panel.add(btn);
        }
        add(panel, BorderLayout.CENTER);

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
        if ("0123456789".contains(cmd)) {
            if (startNewNumber || display.getText().equals("0")) {
                expression.setLength(0);
                expression.append(cmd);
                startNewNumber = false;
            } else {
                expression.append(cmd);
            }
            display.setText(expression.toString());
        } else if ("+-*/".contains(cmd)) {
            if (!startNewNumber) {
                expression.append(cmd);
                display.setText(expression.toString());
                startNewNumber = true;
            }
        } else if (cmd.equals(".")) {
            if (startNewNumber) {
                expression.setLength(0);
                expression.append("0.");
                startNewNumber = false;
            } else if (!getLastNumber(expression.toString()).contains(".")) {
                expression.append(".");
            }
            display.setText(expression.toString());
        } else if (cmd.equals("C")) {
            expression.setLength(0);
            expression.append("0");
            display.setText("0");
            startNewNumber = true;
        } else if (cmd.equals("←")) {
            if (!startNewNumber && expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
                if (expression.length() == 0) {
                    expression.append("0");
                    startNewNumber = true;
                }
                display.setText(expression.toString());
            }
        } else if (cmd.equals("±")) {
            togglePlusMinus();
        } else if (cmd.equals("=")) {
            calculate();
        } else if (cmd.equals("(")) {
            if (startNewNumber || display.getText().equals("0")) {
                expression.setLength(0);
                expression.append("(");
                startNewNumber = false;
            } else {
                expression.append("(");
            }
            display.setText(expression.toString());
        } else if (cmd.equals(")")) {
            expression.append(")");
            display.setText(expression.toString());
        }
    }

    private void calculate() {
        String expr = expression.toString();
        while (expr.endsWith("+") || expr.endsWith("-") || expr.endsWith("*") || expr.endsWith("/")) {
            expr = expr.substring(0, expr.length() - 1);
        }
        try {
            double result = evaluateExpression(expr);
            String resultStr = (result == (long) result) ? String.valueOf((long) result) : String.valueOf(result);
            display.setText(resultStr);
            expression.setLength(0);
            expression.append(resultStr);
            startNewNumber = true;
        } catch (Exception e) {
            display.setText("Error");
            expression.setLength(0);
            expression.append("0");
            startNewNumber = true;
        }
    }

    private double evaluateExpression(String expr) throws Exception {
        return new ExprParser(expr).parse();
    }

    private static class ExprParser {
        String expr;
        int pos = 0;

        ExprParser(String expr) {
            this.expr = expr;
        }

        double parse() throws Exception {
            double result = parseAddSubtract();
            if (pos < expr.length()) throw new Exception("Unexpected character at position " + pos);
            return result;
        }

        double parseAddSubtract() throws Exception {
            double result = parseMultiplyDivide();
            while (pos < expr.length() && (expr.charAt(pos) == '+' || expr.charAt(pos) == '-')) {
                char op = expr.charAt(pos++);
                double right = parseMultiplyDivide();
                result = op == '+' ? result + right : result - right;
            }
            return result;
        }

        double parseMultiplyDivide() throws Exception {
            double result = parseUnary();
            while (pos < expr.length() && (expr.charAt(pos) == '*' || expr.charAt(pos) == '/')) {
                char op = expr.charAt(pos++);
                double right = parseUnary();
                result = op == '*' ? result * right : result / right;
            }
            return result;
        }

        double parseUnary() throws Exception {
            if (pos < expr.length() && expr.charAt(pos) == '-') {
                pos++;
                return -parseUnary();
            }
            return parsePrimary();
        }

        double parsePrimary() throws Exception {
            if (pos < expr.length() && expr.charAt(pos) == '(') {
                pos++;
                double result = parseAddSubtract();
                if (pos >= expr.length() || expr.charAt(pos) != ')') 
                    throw new Exception("Missing closing parenthesis");
                pos++;
                return result;
            }
            return parseNumber();
        }

        double parseNumber() throws Exception {
            int start = pos;
            if (pos < expr.length() && expr.charAt(pos) == '-') pos++;
            while (pos < expr.length() && Character.isDigit(expr.charAt(pos))) pos++;
            if (pos < expr.length() && expr.charAt(pos) == '.') {
                pos++;
                while (pos < expr.length() && Character.isDigit(expr.charAt(pos))) pos++;
            }
            if (start == pos) throw new Exception("Invalid number at position " + pos);
            return Double.parseDouble(expr.substring(start, pos));
        }
    }

    private void togglePlusMinus() {
        String expr = expression.toString();
        int[] lastIdx = getLastNumberIndex(expr);
        if (lastIdx[0] >= lastIdx[1]) return;
        int start = lastIdx[0];
        int end = lastIdx[1];
        String number = expr.substring(start, end);
        if (number.isEmpty() || number.equals("0")) return;
        StringBuilder newExpr = new StringBuilder();
        newExpr.append(expr, 0, start);
        if (number.startsWith("-")) {
            newExpr.append(number.substring(1));
        } else {
            newExpr.append("-").append(number);
        }
        if (end < expr.length()) {
            newExpr.append(expr.substring(end));
        }
        expression = newExpr;
        display.setText(expression.toString());
    }

    private String getLastNumber(String expr) {
        int i = expr.length() - 1;
        while (i >= 0 && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) i--;
        return expr.substring(i + 1);
    }

    private int[] getLastNumberIndex(String expr) {
        int end = expr.length();
        int i = end - 1;
        int par = 0;
        while (i >= 0) {
            char c = expr.charAt(i);
            if (c == ')') par++;
            if (c == '(') par--;
            if (Character.isDigit(c) || c == '.' || (c == '-' && (i == 0 || "+-*/(".contains("" + expr.charAt(i-1))))) {
                if (par == 0) i--;
                else break;
            } else break;
        }
        int start = i + 1;
        return new int[] { start, end };
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isDigit(c)) {
            handleInput("" + c);
        } else if ("+-*/".indexOf(c) != -1) {
            handleInput("" + c);
        } else if (c == '\n' || c == '=') {
            handleInput("=");
        } else if (c == '.' || c == ',') {
            handleInput(".");
        } else if (c == '(') {
            handleInput("(");
        } else if (c == ')') {
            handleInput(")");
        } else if (c == 'c' || c == 'C' || c == 27) {
            handleInput("C");
        } else if (c == '\b') {
            handleInput("←");
        } else if (c == 'n' || c == 'N') {
            handleInput("±");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}