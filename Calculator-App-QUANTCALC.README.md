QuantCalc (standalone folder)
=============================

This folder contains a standalone copy of the QuantCalc Java Swing calculator.

Quick start (Windows):

1. Compile:

```powershell
cd "C:\Users\Admin\OneDrive\Desktop\QUANTCALC"
"C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javac.exe" Calculator.java
```

2. Run (detached):

```powershell
start "" "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javaw.exe" -cp . Calculator
```

Or use the provided helper scripts:

- `compile.cmd` — compile the source
- `run.cmd` — run in a console
- `launch_calculator.cmd` — launch without opening a console window
- `create_shortcut.ps1` — PowerShell script to create a desktop shortcut

Notes:
- Keep `quantcalc-icon.png` and `quantcalc-icon.ico` next to `Calculator.java` for the app icon to load.
- This folder is not a git repository by default; the canonical repo is `Calculator-App`.
