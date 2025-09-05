#!/bin/bash

# Bash script to setup Git hooks for IWAA project
# Run this script to make the Git hooks executable

echo "🔧 Setting up Git hooks for IWAA project..."

# Check if we're in a Git repository
if [ ! -d ".git" ]; then
    echo "❌ Not in a Git repository. Please run this script from the project root."
    exit 1
fi

# Check if Git hooks directory exists
if [ ! -d ".git/hooks" ]; then
    echo "❌ Git hooks directory not found."
    exit 1
fi

# Make pre-commit hook executable
if [ -f ".git/hooks/pre-commit" ]; then
    chmod +x ".git/hooks/pre-commit"
    echo "✅ Pre-commit hook configured and made executable"
else
    echo "❌ Pre-commit hook not found"
fi

# Make pre-push hook executable
if [ -f ".git/hooks/pre-push" ]; then
    chmod +x ".git/hooks/pre-push"
    echo "✅ Pre-push hook configured and made executable"
else
    echo "❌ Pre-push hook not found"
fi

echo "🎉 Git hooks setup complete!"
echo ""
echo "The following hooks are now active:"
echo "  • pre-commit: Runs Detekt, Lint, and Tests before each commit"
echo "  • pre-push: Runs comprehensive checks before pushing to main/master"
echo ""
echo "To test the hooks, try making a commit or push."
echo "To bypass hooks temporarily, use: git commit --no-verify or git push --no-verify"
