# QuantCalc

QuantCalc is a Java Swing desktop scientific calculator built to solve everyday arithmetic, advanced expressions, and educational problems from a clean desktop interface.

## What this app does

QuantCalc evaluates expressions using an internal parser, offers historical calculation review, and provides a simple formula assistant for common topics such as geometry and finance.

## Key features

- Basic arithmetic: `+`, `-`, `*`, `/`
- Parentheses and exponentiation: `^`
- Scientific functions: `sin`, `cos`, `tan`, `sqrt`, `log`, `ln`
- Inverse trig support: `asin`, `acos`, `atan` via `Inv` mode
- Degree/radian toggle: `Deg` / `Rad`
- Factorial support: `!` (e.g. `5!`)
- Constants: `π` and `e`
- `Ans` support for chaining results
- Memory controls: `MC`, `MR`, `M+`, `M-`
- Step-by-step explanation mode
- Calculation history with detailed steps
- Formula assistant for geometric and financial formulas
- Light/dark theme switch with desktop styling
- Built-in parser; no external libraries required

## Recent updates

This project now includes:

- `Inv` mode for inverse trigonometric functions
- `Deg` toggle for degrees/radians input handling
- `!` factorial parsing and evaluation
- Improved explain view that displays actual calculation steps
- Updated history and step display for saved expressions

## Requirements

- Java 8 or newer
- No external dependencies

## Run instructions

From `Calculator-App`:

```powershell
cd "C:\Users\Admin\OneDrive\Desktop\Calculator-App"
"C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javac.exe" Calculator.java
"C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\java.exe" Calculator
```

Or if `java` and `javac` are on your PATH:

```powershell
cd "C:\Users\Admin\OneDrive\Desktop\Calculator-App"
javac Calculator.java
java Calculator
```

## Usage tips

- Enter expressions with the on-screen buttons or keyboard
- Press `=` to evaluate
- Click `Deg` to switch between degrees and radians
- Click `Inv` to enable inverse trig functions
- Use `5!` for factorial
- Open the history panel to review past expressions and steps
- Use `Explain` to see the detailed step-by-step solution

## Files in this folder

- `Calculator.java` — main application source
- `README.md` — project overview and usage guide
- `launch_calculator.cmd` / `launch_quantcalc.cmd` — helper launch scripts

## GitHub deployment note

This folder is not currently a git repository, so I cannot push directly to GitHub from here. To publish this documentation to your GitHub account, initialize git in `Calculator-App`, commit the files, and push to your repository:

```powershell
git init
git add README.md Calculator.java
git commit -m "Add QuantCalc README and documentation"
git remote add origin <your-github-repo-url>
git push -u origin main
```

Once your repository exists on GitHub, your updated README will show up there.