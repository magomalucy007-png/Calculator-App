@echo off
cd /d "%~dp0"
rem Launch QuantCalc using javaw if available to avoid console window
if exist "%ProgramFiles%\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javaw.exe" (
  "%ProgramFiles%\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javaw.exe" -cp . Calculator
) else (
  start "" javaw -cp . Calculator
)
