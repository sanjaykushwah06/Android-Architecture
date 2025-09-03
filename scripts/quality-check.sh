#!/bin/bash

# Quality Check Script for IIWA App
# This script runs all quality checks locally before committing

set -e  # Exit on any error

echo "🔍 Starting Quality Checks for IIWA App..."
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "gradlew" ]; then
    print_error "gradlew not found. Please run this script from the project root directory."
    exit 1
fi

# Make gradlew executable
chmod +x ./gradlew

print_status "Running Detekt code analysis..."
if ./gradlew detekt; then
    print_success "Detekt passed! ✅"
else
    print_error "Detekt failed! ❌"
    print_warning "Please fix the issues above before committing."
    exit 1
fi

print_status "Running Android Lint..."
if ./gradlew app:lintDebug; then
    print_success "Lint passed! ✅"
else
    print_error "Lint failed! ❌"
    print_warning "Please fix the issues above before committing."
    exit 1
fi

print_status "Running unit tests..."
if ./gradlew testDebugUnitTest; then
    print_success "Unit tests passed! ✅"
else
    print_error "Unit tests failed! ❌"
    print_warning "Please fix the failing tests before committing."
    exit 1
fi

print_status "Running build check..."
if ./gradlew assembleDebug; then
    print_success "Build successful! ✅"
else
    print_error "Build failed! ❌"
    print_warning "Please fix the build issues before committing."
    exit 1
fi

echo ""
echo "🎉 All quality checks passed!"
echo "✅ Detekt: No code quality issues"
echo "✅ Lint: No Android lint issues"
echo "✅ Tests: All unit tests passing"
echo "✅ Build: Project builds successfully"
echo ""
echo "🚀 You're ready to commit and push!"
echo "=========================================="
