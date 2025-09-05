/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - API configuration view model for managing API endpoints and settings
 */

package com.iiwa.viewmodels

import android.content.Context
import com.iiwa.data.api.ApiConfig
import com.iiwa.data.api.ApiEnvironment
import com.iiwa.data.api.ServiceGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ApiConfigViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val apiConfig: ApiConfig
) : BaseViewModel<ApiConfigUiState>(context) {
    private val _uiState = MutableStateFlow(ApiConfigUiState())
    override val uiState: StateFlow<ApiConfigUiState> = _uiState.asStateFlow()

    init {
        loadCurrentConfiguration()
    }

    private fun loadCurrentConfiguration() {
        _uiState.value = _uiState.value.copy(
            currentEnvironment = apiConfig.getCurrentEnvironment(),
            availableEnvironments = apiConfig.getAvailableEnvironments(),
            currentBaseUrl = ServiceGenerator.getBaseUrl(),
            isLoading = false
        )
    }

    /**
     * Switch to a different environment
     */
    fun switchEnvironment(environment: String) {
        try {
            apiConfig.switchEnvironment(environment)
            loadCurrentConfiguration()
        } catch (e: IllegalArgumentException) {
            android.util.Log.w("ApiConfigViewModel", "Invalid environment: $environment", e)
            // Handle invalid environment
        }
    }

    /**
     * Set custom base URL
     */
    fun setCustomBaseUrl(customUrl: String) {
        apiConfig.setCustomBaseUrl(customUrl)
        loadCurrentConfiguration()
    }

    /**
     * Restore saved base URL
     */
    fun restoreSavedBaseUrl() {
        apiConfig.restoreSavedBaseUrl()
        loadCurrentConfiguration()
    }

    /**
     * Set API key
     */
    fun setApiKey(apiKey: String) {
        apiConfig.setApiKey(apiKey)
    }

    /**
     * Get current API key
     */
    fun getApiKey(): String? {
        return apiConfig.getApiKey()
    }

    /**
     * Set custom timeout
     */
    fun setTimeout(timeout: Long) {
        apiConfig.setTimeout(timeout)
    }

    /**
     * Get current timeout
     */
    fun getTimeout(): Long {
        return apiConfig.getTimeout()
    }

    /**
     * Set retry count
     */
    fun setRetryCount(retryCount: Int) {
        apiConfig.setRetryCount(retryCount)
    }

    /**
     * Get current retry count
     */
    fun getRetryCount(): Int {
        return apiConfig.getRetryCount()
    }

    /**
     * Check if current environment is production
     */
    fun isProduction(): Boolean = apiConfig.isProduction()

    /**
     * Check if current environment is mock
     */
    fun isMock(): Boolean = apiConfig.isMock()

    /**
     * Check if current environment is local
     */
    fun isLocal(): Boolean = apiConfig.isLocal()

    /**
     * Get environment configuration by name
     */
    fun getEnvironmentConfig(environment: String): ApiEnvironment? {
        return apiConfig.getEnvironmentConfig(environment)
    }

    /**
     * Refresh configuration
     */
    fun refreshConfiguration() {
        loadCurrentConfiguration()
    }
}

data class ApiConfigUiState(
    val currentEnvironment: ApiEnvironment? = null,
    val availableEnvironments: List<String> = emptyList(),
    val currentBaseUrl: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
