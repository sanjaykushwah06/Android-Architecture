# CI/CD Setup for IIWA App

This document explains the CI/CD pipeline setup for the IIWA Android application, including code quality checks, automated testing, and deployment workflows.

## 🚀 Overview

The CI/CD pipeline ensures code quality and prevents broken code from being merged by running:
1. **Detekt** - Static code analysis for Kotlin
2. **Android Lint** - Android-specific code analysis
3. **Unit Tests** - Automated testing (runs in all workflows)
4. **Build Verification** - Ensures the project compiles successfully

## 📁 Workflow Files

### 1. Code Quality Gate (`.github/workflows/code-quality-gate.yml`)
- **Triggers**: Push to any branch, Pull Requests to main/develop
- **Purpose**: Runs Detekt, Lint, and Unit Tests
- **Behavior**: Fails the pipeline if any quality issues or test failures are found
- **Reports**: Uploads quality and test reports as artifacts

### 2. Build and Deploy (`.github/workflows/build-and-deploy.yml`)
- **Triggers**: Push to main/develop branches, Pull Requests to main
- **Purpose**: Builds the application and runs tests
- **Dependencies**: Only runs after quality checks pass
- **Artifacts**: Uploads APK files and test reports

### 3. Main CI Pipeline (`.github/workflows/ci.yml`)
- **Triggers**: Push to any branch, Pull Requests, Manual trigger
- **Purpose**: Complete CI pipeline with all checks
- **Jobs**: Quality checks (includes tests) → Build (sequential execution)

## 🛠️ Local Development

### Running Quality Checks Locally

#### Option 1: Using the Quality Check Script
```bash
# For Unix/Linux/macOS
./scripts/quality-check.sh

# For Windows
scripts\quality-check.bat
```

#### Option 2: Manual Commands
```bash
# Run Detekt
./gradlew detekt

# Run Lint
./gradlew app:lintDebug

# Run Tests
./gradlew testDebugUnitTest

# Build Project
./gradlew assembleDebug
```

#### Option 3: Using Pre-commit Hooks
```bash
# Install pre-commit
pip install pre-commit

# Install hooks
pre-commit install

# Now quality checks run automatically before each commit
```

## 📊 Quality Reports

### Detekt Reports
- **Location**: `app/build/reports/detekt/`
- **Formats**: HTML, XML, TXT, Markdown
- **View**: Open `detekt.html` in your browser

### Lint Reports
- **Location**: `app/build/reports/`
- **Formats**: HTML, XML
- **View**: Open `lint-results-debug.html` in your browser

## 🔧 Configuration Files

### Detekt Configuration
- **File**: `config/detekt.yml`
- **Purpose**: Defines code quality rules and thresholds
- **Customization**: Modify rules, thresholds, and exclusions

### Lint Configuration
- **File**: `config/lint.xml`
- **Purpose**: Defines Android-specific lint rules
- **Customization**: Enable/disable specific checks

## 🚦 Pipeline Behavior

### On Push to Any Branch
1. ✅ Code Quality Gate runs
2. ✅ If quality checks pass → Build and Deploy runs
3. ❌ If quality checks fail → Pipeline stops, no build

### On Pull Request
1. ✅ Code Quality Gate runs
2. ✅ PR comment with quality results
3. ✅ If quality checks pass → Build verification
4. ❌ If quality checks fail → PR cannot be merged

### Branch Protection (Recommended)
Set up branch protection rules in GitHub:
1. Go to Settings → Branches
2. Add rule for `main` and `develop` branches
3. Enable "Require status checks to pass before merging"
4. Select the "Code Quality Gate" check

## 🐛 Troubleshooting

### Common Issues

#### 1. Detekt Failures
```bash
# Check specific violations
./gradlew detekt --info

# Generate baseline (for existing projects)
./gradlew detektBaseline
```

#### 2. Lint Failures
```bash
# Check specific issues
./gradlew app:lintDebug --info

# Generate baseline
./gradlew app:lintBaseline
```

#### 3. Build Failures
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

### Quality Check Script Issues
- Ensure you're running from the project root directory
- Make sure `gradlew` is executable (Unix systems)
- Check that Java 17 is installed and configured

## 📈 Metrics and Monitoring

### Quality Metrics Tracked
- **Code Complexity**: Cyclomatic complexity, method length
- **Code Style**: Naming conventions, formatting
- **Best Practices**: Android-specific recommendations
- **Security**: Potential security vulnerabilities
- **Performance**: Performance-related issues

### Reports Location
- **GitHub Actions**: Check the Actions tab in your repository
- **Artifacts**: Download reports from the Actions run page
- **Local**: Check `app/build/reports/` directory

## 🔄 Continuous Improvement

### Regular Tasks
1. **Review Quality Reports**: Check for recurring issues
2. **Update Rules**: Adjust Detekt and Lint configurations
3. **Monitor Metrics**: Track quality trends over time
4. **Team Training**: Share best practices with the team

### Customization
- Modify `config/detekt.yml` for custom Kotlin rules
- Update `config/lint.xml` for Android-specific checks
- Adjust workflow triggers in `.github/workflows/`
- Add custom quality checks to the pipeline

## 📞 Support

If you encounter issues with the CI/CD setup:
1. Check the GitHub Actions logs
2. Review the quality reports
3. Consult this documentation
4. Contact the development team

---

**Remember**: The goal is to maintain high code quality while providing fast feedback to developers. The pipeline should catch issues early and provide clear guidance on how to fix them.
