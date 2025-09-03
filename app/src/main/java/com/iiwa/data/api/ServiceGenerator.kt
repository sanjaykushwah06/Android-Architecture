/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Service generator for creating Retrofit API service instances
 */

package com.iiwa.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {
    
    // Current base URL - initialized to DummyJSON for authentication
    private var currentBaseUrl: String = "https://dummyjson.com/"
    
    // Timeout configurations from environment config
    private val environmentConfig = BuildConfig.getEnvironmentConfig()
    
    /**
     * Set the base URL for API calls
     */
    fun setBaseUrl(baseUrl: String) {
        currentBaseUrl = baseUrl
    }
    
    /**
     * Get the current base URL
     */
    fun getBaseUrl(): String = currentBaseUrl
    
    /**
     * Reset to build-time base URL
     */
    fun resetToBuildConfig() {
        currentBaseUrl = BuildConfig.BASE_URL
    }
    
    /**
     * Switch to production environment
     */
    fun useProduction() {
        setBaseUrl("https://api.production.com/")
    }
    
    /**
     * Switch to staging environment
     */
    fun useStaging() {
        setBaseUrl("https://api.staging.com/")
    }
    
    /**
     * Switch to development environment
     */
    fun useDevelopment() {
        setBaseUrl("https://api.dev.com/")
    }
    
    /**
     * Switch to mock API (JSONPlaceholder)
     */
    fun useMock() {
        setBaseUrl("https://jsonplaceholder.typicode.com/")
    }
    
    /**
     * Switch to DummyJSON API for authentication
     */
    fun useDummyJson() {
        setBaseUrl("https://dummyjson.com/")
    }
    
    /**
     * Switch to local development server
     */
    fun useLocal() {
        setBaseUrl("http://10.0.2.2:8080/")
    }
    
    /**
     * Create a custom base URL
     */
    fun useCustomUrl(customUrl: String) {
        setBaseUrl(customUrl)
    }
    
    /**
     * Create Gson instance with custom configuration
     */
    private fun createGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }
    
    /**
     * Create OkHttpClient with interceptors and timeouts
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.ENABLE_LOGGING) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(environmentConfig.timeout, TimeUnit.SECONDS)
            .readTimeout(environmentConfig.timeout, TimeUnit.SECONDS)
            .writeTimeout(environmentConfig.timeout, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Create Retrofit instance with current base URL
     */
    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }
    
    /**
     * Create API service instance
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return createRetrofit().create(serviceClass)
    }
    
    /**
     * Create API service instance with custom base URL
     */
    fun <T> createService(serviceClass: Class<T>, baseUrl: String): T {
        val originalUrl = currentBaseUrl
        setBaseUrl(baseUrl)
        val service = createRetrofit().create(serviceClass)
        setBaseUrl(originalUrl) // Restore original URL
        return service
    }
    
    /**
     * Check if current base URL is mock
     */
    fun isMockEnvironment(): Boolean = currentBaseUrl == "https://jsonplaceholder.typicode.com/"
    
    /**
     * Check if current base URL is DummyJSON
     */
    fun isDummyJsonEnvironment(): Boolean = currentBaseUrl == "https://dummyjson.com/"
    
    /**
     * Check if current base URL is local
     */
    fun isLocalEnvironment(): Boolean = currentBaseUrl == "http://10.0.2.2:8080/"
    
    /**
     * Check if current base URL is production
     */
    fun isProductionEnvironment(): Boolean = currentBaseUrl == "https://api.production.com/"
    
    /**
     * Check if current base URL matches build config
     */
    fun isBuildConfigEnvironment(): Boolean = currentBaseUrl == BuildConfig.BASE_URL
    
    /**
     * Get current environment info
     */
    fun getCurrentEnvironmentInfo(): String {
        return when {
            isBuildConfigEnvironment() -> "Build Config: ${BuildConfig.ENVIRONMENT}"
            isProductionEnvironment() -> "Production (Override)"
            isStagingEnvironment() -> "Staging (Override)"
            isDevelopmentEnvironment() -> "Development (Override)"
            isMockEnvironment() -> "Mock API"
            isDummyJsonEnvironment() -> "DummyJSON API"
            isLocalEnvironment() -> "Local Development"
            else -> "Custom: $currentBaseUrl"
        }
    }
    
    /**
     * Check if current base URL is staging
     */
    private fun isStagingEnvironment(): Boolean = currentBaseUrl == "https://api.staging.com/"
    
    /**
     * Check if current base URL is development
     */
    private fun isDevelopmentEnvironment(): Boolean = currentBaseUrl == "https://api.dev.com/"
}
