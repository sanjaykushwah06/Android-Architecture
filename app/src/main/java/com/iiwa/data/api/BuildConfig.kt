package com.iiwa.data.api

/**
 * Build configuration constants that are automatically generated based on product flavors.
 * This class provides access to environment-specific configuration values.
 * 
 * Note: The actual values are injected at build time by the Android Gradle Plugin
 * based on the selected product flavor (development, staging, or production).
 */
object BuildConfig {
    
    /**
     * Base URL for API calls - varies by environment
     */
    const val BASE_URL: String = com.iiwa.BuildConfig.BASE_URL
    
    /**
     * Current environment name
     */
    const val ENVIRONMENT: String = com.iiwa.BuildConfig.ENVIRONMENT
    
    /**
     * Whether logging is enabled for this build
     */
    const val ENABLE_LOGGING: Boolean = com.iiwa.BuildConfig.ENABLE_LOGGING
    
    /**
     * Whether crash reporting is enabled for this build
     */
    const val ENABLE_CRASH_REPORTING: Boolean = com.iiwa.BuildConfig.ENABLE_CRASH_REPORTING
    
    /**
     * Check if current build is development
     */
    val isDevelopment: Boolean
        get() = ENVIRONMENT == "development"
    
    /**
     * Check if current build is staging
     */
    val isStaging: Boolean
        get() = ENVIRONMENT == "staging"
    
    /**
     * Check if current build is production
     */
    val isProduction: Boolean
        get() = ENVIRONMENT == "production"
    
    /**
     * Get environment-specific configuration
     */
    fun getEnvironmentConfig(): EnvironmentConfig {
        return when (ENVIRONMENT) {
            "development" -> EnvironmentConfig(
                baseUrl = BASE_URL,
                enableLogging = true,
                enableCrashReporting = false,
                timeout = 60L,
                retryCount = 5
            )
            "staging" -> EnvironmentConfig(
                baseUrl = BASE_URL,
                enableLogging = true,
                enableCrashReporting = true,
                timeout = 30L,
                retryCount = 3
            )
            "production" -> EnvironmentConfig(
                baseUrl = BASE_URL,
                enableLogging = false,
                enableCrashReporting = true,
                timeout = 30L,
                retryCount = 3
            )
            else -> EnvironmentConfig(
                baseUrl = BASE_URL,
                enableLogging = true,
                enableCrashReporting = false,
                timeout = 30L,
                retryCount = 3
            )
        }
    }
}

/**
 * Data class representing environment-specific configuration
 */
data class EnvironmentConfig(
    val baseUrl: String,
    val enableLogging: Boolean,
    val enableCrashReporting: Boolean,
    val timeout: Long,
    val retryCount: Int
)
