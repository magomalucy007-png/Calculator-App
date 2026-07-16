# QuantCalc Documentation

## Overview

QuantCalc is a Java Swing desktop scientific calculator designed for both casual use and educational learning. It offers core arithmetic, scientific functions, inverse trigonometry, factorial support, and a friendly desktop interface with history and step-by-step explanation.

## Core purpose

The app aims to be a self-contained calculator with:

- A built-in expression parser for reliable evaluation
- A clean desktop GUI with scientific keys and history
- Step-by-step explanations for computation transparency
- Support for formulas, challenges, and memory operations

## Current features

- Arithmetic: `+`, `-`, `*`, `/`
- Parentheses and exponentiation: `^`
- Scientific functions: `sin`, `cos`, `tan`, `sqrt`, `log`, `ln`
- Inverse trig: `asin`, `acos`, `atan` via an `Inv` toggle
- Degree / radian mode with `Deg` / `Rad` toggle
- Factorial: `!` (e.g. `5!`)
- Constants: `π`, `e`, and `Ans`
- Memory buttons: `MC`, `MR`, `M+`, `M-`
- Step explanation and history panel
- Formula assistant for geometric and financial calculations
- Light / dark theme support

## Recent improvements

- Added explicit `Inv` toggle for inverse trig entry
- Added `Deg` / `Rad` mode handling in parser
- Added factorial operator `!` support
- Corrected Explain to show actual intermediate steps instead of only final answer
- Preserved the original expression when explaining results

## How to run

1. Open a terminal in `Calculator-App`
2. Compile:
   ```powershell
   javac Calculator.java
   ```
3. Run:
   ```powershell
   java Calculator
   ```

If Java is not on PATH, use the full JDK path reported on your system.

## How to publish to GitHub

This workspace does not currently have git initialized in `Calculator-App`. To publish:

```powershell
git init
 git add README.md Documentation.md Calculator.java
 git commit -m "Add QuantCalc documentation"
 git remote add origin <your-github-repo-url>
 git branch -M main
 git push -u origin main
```

## Notes

- The current app uses a custom parser and does not rely on JavaScript or external parsing libraries.
- `Calculator-App/Calculator.java` is the main application source and the file being edited for feature updates.
- The app launch commands are also available via `launch_calculator.cmd` and `launch_quantcalc.cmd` in the same folder.
