@echo off
REM IWA Project Environment Setup Script
REM This script sets up the local environment for the IWA project

echo Setting up IWA project environment...

REM Set JAVA_HOME for this project
set JAVA_HOME=C:\Program Files\Java\jdk-17

REM Verify JAVA_HOME is set correctly
if exist "%JAVA_HOME%" (
    echo ✓ JAVA_HOME set to: %JAVA_HOME%
) else (
    echo ✗ JAVA_HOME path does not exist: %JAVA_HOME%
    echo Please update the JAVA_HOME path in this script to match your JDK installation
    pause
    exit /b 1
)

REM Add Java bin to PATH for this session
set PATH=%JAVA_HOME%\bin;%PATH%

echo ✓ Java bin added to PATH

REM Display current Java version
java -version 2>&1 | findstr "version"
if %errorlevel% equ 0 (
    echo ✓ Java version verified
) else (
    echo ✗ Could not verify Java version
)

echo Environment setup complete!
echo You can now run Gradle commands like: gradlew build
