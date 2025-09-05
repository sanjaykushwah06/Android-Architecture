#!/bin/bash
# IWA Project Environment Setup Script for Unix-like systems
# This script sets up the local environment for the IWA project

echo "Setting up IWA project environment..."

# Set JAVA_HOME for this project
export JAVA_HOME="C:/Program Files/Java/jdk-17"

# Verify JAVA_HOME is set correctly
if [ -d "$JAVA_HOME" ]; then
    echo "✓ JAVA_HOME set to: $JAVA_HOME"
else
    echo "✗ JAVA_HOME path does not exist: $JAVA_HOME"
    echo "Please update the JAVA_HOME path in this script to match your JDK installation"
    exit 1
fi

# Add Java bin to PATH for this session
export PATH="$JAVA_HOME/bin:$PATH"

echo "✓ Java bin added to PATH"

# Display current Java version
if command -v java >/dev/null 2>&1; then
    java_version=$(java -version 2>&1 | head -n 1)
    echo "✓ Java version: $java_version"
else
    echo "✗ Could not verify Java version"
fi

echo "Environment setup complete!"
echo "You can now run Gradle commands like: ./gradlew build"
