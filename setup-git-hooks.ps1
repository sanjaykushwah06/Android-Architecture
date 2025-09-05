# PowerShell script to setup Git hooks for IWAA project
# Run this script to make the Git hooks executable

Write-Host "🔧 Setting up Git hooks for IWAA project..." -ForegroundColor Blue

# Check if we're in a Git repository
if (-not (Test-Path ".git")) {
    Write-Host "❌ Not in a Git repository. Please run this script from the project root." -ForegroundColor Red
    exit 1
}

# Check if Git hooks directory exists
if (-not (Test-Path ".git/hooks")) {
    Write-Host "❌ Git hooks directory not found." -ForegroundColor Red
    exit 1
}

# Make pre-commit hook executable
if (Test-Path ".git/hooks/pre-commit") {
    # On Windows, we need to ensure the file has proper line endings and is executable
    Write-Host "✅ Pre-commit hook found" -ForegroundColor Green
    
    # Convert line endings to Unix format if needed
    $content = Get-Content ".git/hooks/pre-commit" -Raw
    $content = $content -replace "`r`n", "`n"
    Set-Content ".git/hooks/pre-commit" -Value $content -NoNewline
    
    Write-Host "✅ Pre-commit hook configured" -ForegroundColor Green
} else {
    Write-Host "❌ Pre-commit hook not found" -ForegroundColor Red
}

# Make pre-push hook executable
if (Test-Path ".git/hooks/pre-push") {
    Write-Host "✅ Pre-push hook found" -ForegroundColor Green
    
    # Convert line endings to Unix format if needed
    $content = Get-Content ".git/hooks/pre-push" -Raw
    $content = $content -replace "`r`n", "`n"
    Set-Content ".git/hooks/pre-push" -Value $content -NoNewline
    
    Write-Host "✅ Pre-push hook configured" -ForegroundColor Green
} else {
    Write-Host "❌ Pre-push hook not found" -ForegroundColor Red
}

Write-Host "🎉 Git hooks setup complete!" -ForegroundColor Green
Write-Host ""
Write-Host "The following hooks are now active:" -ForegroundColor Yellow
Write-Host "  • pre-commit: Runs Detekt, Lint, and Tests before each commit" -ForegroundColor White
Write-Host "  • pre-push: Runs comprehensive checks before pushing to main/master" -ForegroundColor White
Write-Host ""
Write-Host "To test the hooks, try making a commit or push." -ForegroundColor Cyan
Write-Host "To bypass hooks temporarily, use: git commit --no-verify or git push --no-verify" -ForegroundColor Yellow
