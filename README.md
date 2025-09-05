# IWA - Android Architecture

## Environment Setup

This project requires Java 17. To set up the environment:

### Option 1: Automatic Setup (Recommended)
Run the environment setup script before working on the project:

**PowerShell:**
```powershell
.\setup-env.ps1
```

**Command Prompt:**
```cmd
setup-env.bat
```

### Option 2: Manual Setup
Set the following environment variables:
- `JAVA_HOME=C:\Program Files\Java\jdk-17`
- Add `%JAVA_HOME%\bin` to your PATH

### Option 3: Gradle Configuration
The project is already configured in `gradle.properties` to use Java 17:
```properties
org.gradle.java.home=C\:\\Program Files\\Java\\jdk-17
```

## Building the Project

After setting up the environment:
```bash
./gradlew build
```

## Running Tests

```bash
./gradlew test
```

## Code Quality

The project uses Detekt for static analysis:
```bash
./gradlew detekt
```