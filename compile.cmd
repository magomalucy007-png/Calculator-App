@echo off
cd /d "%~dp0"
rem Compile Calculator.java using configured JDK or system javac
if exist "%ProgramFiles%\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javac.exe" (
  "%ProgramFiles%\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javac.exe" Calculator.java
) else (
  javac Calculator.java
)
