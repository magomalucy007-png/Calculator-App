# Java Calculator Application

A simple graphical calculator built with Java Swing that supports basic arithmetic operations, parentheses, and decimal numbers.

## Features

- **Basic Operations**: Addition (+), Subtraction (-), Multiplication (*), Division (/)
- **Parentheses**: Support for grouping expressions with ( and )
- **Decimal Numbers**: Handle floating-point calculations
- **Plus/Minus Toggle**: Change the sign of the last number with ±
- **Clear and Backspace**: Reset the calculator or remove the last input
- **Keyboard Support**: Full keyboard input support for all functions
- **Real-time Display**: Expression updates as you type

## Requirements

- Java 8 or higher (tested on Java 25)
- No external dependencies

## How to Run

1. Compile the Java file:
   ```
   javac Calculator.java
   ```

2. Run the application:
   ```
   java Calculator
   ```

A GUI window will open with the calculator interface.

## Usage

- Click buttons or use keyboard to input expressions
- Press "=" or Enter to calculate
- Use "C" to clear, "←" to backspace
- Keyboard shortcuts: digits, operators, Enter (=), Backspace (←), Escape (C), N (±), ( and )

## Project Structure

- `Calculator.java`: Main source file containing the entire application

## Development and Fixes

### Original Issue
The original code used Java's `javax.script.ScriptEngine` (Nashorn) for expression evaluation. However, Nashorn was removed in Java 15+, causing the "=" button to fail in modern Java versions.

### Solution Implemented
Replaced the ScriptEngine with a custom arithmetic expression parser using the shunting-yard algorithm:

1. **Tokenization**: Break the expression into numbers, operators, and parentheses
2. **Parsing**: Use stacks to handle operator precedence and parentheses
3. **Evaluation**: Compute the result with proper order of operations

### Key Changes Made
- Removed `javax.script` imports
- Added `java.util` for collections
- Implemented `evaluate()`, `tokenize()`, `evaluateTokens()`, and helper methods
- Updated `calculate()` to use the new evaluator
- Fixed compilation issues with fully qualified class names

### Algorithm Details
- **Shunting-yard Algorithm**: Converts infix notation to postfix for evaluation
- **Operator Precedence**: * and / have higher precedence than + and -
- **Error Handling**: Catches invalid expressions and division by zero
- **Negative Numbers**: Properly handles numbers like -1.5

## Contributing

Feel free to fork and improve the calculator with additional features like scientific functions or history.

## License

This project is open source. Use as you wish.