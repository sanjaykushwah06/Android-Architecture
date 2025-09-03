/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - API configuration for different environments and endpoints
 */

package com.iiwa.data.api

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PREFS_NAME = "api_config"
        private const val KEY_BASE_URL = "base_url"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_TIMEOUT = "timeout"
        private const val KEY_RETRY_COUNT = "retry_count"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Default configurations based on build config
    private val defaultConfig = mapOf(
        "production" to ApiEnvironment(
            baseUrl = "https://api.production.com/",
            timeout = 30L,
            retryCount = 3,
            enableLogging = false
        ),
        "staging" to ApiEnvironment(
            baseUrl = "https://api.staging.com/",
            timeout = 30L,
            retryCount = 3,
            enableLogging = true
        ),
        "development" to ApiEnvironment(
            baseUrl = "https://api.dev.com/",
            timeout = 60L,
            retryCount = 5,
            enableLogging = true
        ),
        "dummyjson" to ApiEnvironment(
            baseUrl = "https://dummyjson.com/",
            timeout = 30L,
            retryCount = 3,
            enableLogging = true
        )
    )

    /**
     * Get current environment configuration
     */
    fun getCurrentEnvironment(): ApiEnvironment {
        val currentUrl = ServiceGenerator.getBaseUrl()
        return defaultConfig.values.find { it.baseUrl == currentUrl }
            ?: ApiEnvironment(
                baseUrl = currentUrl,
                timeout = BuildConfig.getEnvironmentConfig().timeout,
                retryCount = BuildConfig.getEnvironmentConfig().retryCount,
                enableLogging = BuildConfig.ENABLE_LOGGING
            )
    }

    /**
     * Switch to a predefined environment
     */
    fun switchEnvironment(environment: String) {
        when (environment.lowercase()) {
            "production" -> ServiceGenerator.useProduction()
            "staging" -> ServiceGenerator.useStaging()
            "development" -> ServiceGenerator.useDevelopment()
            "mock" -> ServiceGenerator.useMock()
            "dummyjson" -> ServiceGenerator.useDummyJson()
            "local" -> ServiceGenerator.useLocal()
            "buildconfig" -> ServiceGenerator.resetToBuildConfig()
            else -> throw IllegalArgumentException("Unknown environment: $environment")
        }

        // Save the selected environment
        prefs.edit().putString(KEY_BASE_URL, ServiceGenerator.getBaseUrl()).apply()
    }

    /**
     * Set custom base URL
     */
    fun setCustomBaseUrl(customUrl: String) {
        ServiceGenerator.useCustomUrl(customUrl)
        prefs.edit().putString(KEY_BASE_URL, customUrl).apply()
    }

    /**
     * Get saved base URL from preferences
     */
    fun getSavedBaseUrl(): String? {
        return prefs.getString(KEY_BASE_URL, null)
    }

    /**
     * Restore saved base URL
     */
    fun restoreSavedBaseUrl() {
        getSavedBaseUrl()?.let { url ->
            ServiceGenerator.setBaseUrl(url)
        }
    }

    /**
     * Set API key for authentication
     */
    fun setApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    /**
     * Get API key
     */
    fun getApiKey(): String? {
        return prefs.getString(KEY_API_KEY, null)
    }

    /**
     * Set custom timeout
     */
    fun setTimeout(timeout: Long) {
        prefs.edit().putLong(KEY_TIMEOUT, timeout).apply()
    }

    /**
     * Get custom timeout
     */
    fun getTimeout(): Long {
        return prefs.getLong(KEY_TIMEOUT, getCurrentEnvironment().timeout)
    }

    /**
     * Set retry count
     */
    fun setRetryCount(retryCount: Int) {
        prefs.edit().putInt(KEY_RETRY_COUNT, retryCount).apply()
    }

    /**
     * Get retry count
     */
    fun getRetryCount(): Int {
        return prefs.getInt(KEY_RETRY_COUNT, getCurrentEnvironment().retryCount)
    }

    /**
     * Check if current environment is production
     */
    fun isProduction(): Boolean = ServiceGenerator.isProductionEnvironment()

    /**
     * Check if current environment is mock
     */
    fun isMock(): Boolean = ServiceGenerator.isMockEnvironment()

    /**
     * Check if current environment is local
     */
    fun isLocal(): Boolean = ServiceGenerator.isLocalEnvironment()

    /**
     * Check if current environment matches build config
     */
    fun isBuildConfig(): Boolean = ServiceGenerator.isBuildConfigEnvironment()

    /**
     * Get all available environments
     */
    fun getAvailableEnvironments(): List<String> = listOf(
        "production", "staging", "development", "mock", "dummyjson", "local", "buildconfig"
    )

    /**
     * Get environment configuration by name
     */
    fun getEnvironmentConfig(environment: String): ApiEnvironment? {
        return defaultConfig[environment.lowercase()]
    }

    /**
     * Get build configuration info
     */
    fun getBuildConfigInfo(): String {
        return "Build: ${BuildConfig.ENVIRONMENT}, URL: ${BuildConfig.BASE_URL}, Logging: ${BuildConfig.ENABLE_LOGGING}"
    }
}

/**
 * Data class representing API environment configuration
 */
data class ApiEnvironment(
    val baseUrl: String,
    val timeout: Long,
    val retryCount: Int,
    val enableLogging: Boolean
)
