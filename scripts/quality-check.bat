@echo off
REM Quality Check Script for IIWA App (Windows)
REM This script runs all quality checks locally before committing

echo 🔍 Starting Quality Checks for IIWA App...
echo ==========================================

REM Check if we're in the right directory
if not exist "gradlew.bat" (
    echo [ERROR] gradlew.bat not found. Please run this script from the project root directory.
    exit /b 1
)

echo [INFO] Running Detekt code analysis...
call gradlew.bat detekt
if %errorlevel% neq 0 (
    echo [ERROR] Detekt failed! ❌
    echo [WARNING] Please fix the issues above before committing.
    exit /b 1
)
echo [SUCCESS] Detekt passed! ✅

echo [INFO] Running Android Lint...
call gradlew.bat app:lintDebug
if %errorlevel% neq 0 (
    echo [ERROR] Lint failed! ❌
    echo [WARNING] Please fix the issues above before committing.
    exit /b 1
)
echo [SUCCESS] Lint passed! ✅

echo [INFO] Running unit tests...
call gradlew.bat testDebugUnitTest
if %errorlevel% neq 0 (
    echo [ERROR] Unit tests failed! ❌
    echo [WARNING] Please fix the failing tests before committing.
    exit /b 1
)
echo [SUCCESS] Unit tests passed! ✅

echo [INFO] Running build check...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo [ERROR] Build failed! ❌
    echo [WARNING] Please fix the build issues before committing.
    exit /b 1
)
echo [SUCCESS] Build successful! ✅

echo.
echo 🎉 All quality checks passed!
echo ✅ Detekt: No code quality issues
echo ✅ Lint: No Android lint issues
echo ✅ Tests: All unit tests passing
echo ✅ Build: Project builds successfully
echo.
echo 🚀 You're ready to commit and push!
echo ==========================================
