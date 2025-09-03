// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" // or latest
}
// Ensure latest kotlinx-metadata-jvm is used across all modules
allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
        }
    }
}
detekt {
    config.setFrom("$rootDir/config/detekt.yml")
    buildUponDefaultConfig = true
    allRules = false
}