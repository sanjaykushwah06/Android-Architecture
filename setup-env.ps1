# IWA Project Environment Setup Script
# This script sets up the local environment for the IWA project

Write-Host "Setting up IWA project environment..." -ForegroundColor Green

# Set JAVA_HOME for this project
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# Verify JAVA_HOME is set correctly
if (Test-Path $env:JAVA_HOME) {
    Write-Host "JAVA_HOME set to: $env:JAVA_HOME" -ForegroundColor Green
} else {
    Write-Host "JAVA_HOME path does not exist: $env:JAVA_HOME" -ForegroundColor Red
    Write-Host "Please update the JAVA_HOME path in this script to match your JDK installation" -ForegroundColor Yellow
}

# Add Java bin to PATH for this session
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "Java bin added to PATH" -ForegroundColor Green

# Display current Java version
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "Java version: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "Could not verify Java version" -ForegroundColor Red
}

Write-Host "Environment setup complete!" -ForegroundColor Green
Write-Host "You can now run Gradle commands like: ./gradlew build" -ForegroundColor Cyan
