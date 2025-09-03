package com.iiwa.data.api

import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.net.URL

object ApiUtils {

    // Common HTTP status codes
    object StatusCode {
        const val OK = 200
        const val CREATED = 201
        const val NO_CONTENT = 204
        const val BAD_REQUEST = 400
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val CONFLICT = 409
        const val INTERNAL_SERVER_ERROR = 500
        const val SERVICE_UNAVAILABLE = 503
    }

    // Common content types
    object ContentType {
        val JSON = "application/json".toMediaType()
        val FORM = "application/x-www-form-urlencoded".toMediaType()
        val MULTIPART = "multipart/form-data".toMediaType()
        val TEXT = "text/plain".toMediaType()
    }

    /**
     * Check if response is successful
     */
    fun <T> isSuccessful(response: Response<T>): Boolean {
        return response.isSuccessful
    }

    /**
     * Check if response has data
     */
    fun <T> hasData(response: Response<T>): Boolean {
        return response.isSuccessful && response.body() != null
    }

    /**
     * Get response body safely
     */
    fun <T> getBody(response: Response<T>): T? {
        return if (response.isSuccessful) response.body() else null
    }

    /**
     * Get error message from response
     */
    fun getErrorMessage(response: Response<*>): String {
        return try {
            response.errorBody()?.string() ?: "Unknown error occurred"
        } catch (e: IOException) {
            "Error reading error body: ${e.message}"
        }
    }

    /**
     * Get HTTP status code
     */
    fun getStatusCode(response: Response<*>): Int {
        return response.code()
    }

    /**
     * Check if response is a specific status code
     */
    fun isStatusCode(response: Response<*>, statusCode: Int): Boolean {
        return response.code() == statusCode
    }

    /**
     * Check if response indicates client error (4xx)
     */
    fun isClientError(response: Response<*>): Boolean {
        return response.code() in 400..499
    }

    /**
     * Check if response indicates server error (5xx)
     */
    fun isServerError(response: Response<*>): Boolean {
        return response.code() in 500..599
    }

    /**
     * Check if response indicates network error
     */
    fun isNetworkError(response: Response<*>): Boolean {
        return response.code() == 0 || response.code() == -1
    }

    /**
     * Get all headers from response
     */
    fun getAllHeaders(response: Response<*>): Headers {
        return response.headers()
    }

    /**
     * Get specific header value (returns first occurrence)
     */
    fun getHeader(response: Response<*>, name: String): List<String> {
        return response.headers().values(name)
    }

    /**
     * Get all values for a specific header (useful for headers like Set-Cookie)
     */
    fun getHeaderValues(response: Response<*>, name: String): List<String> {
        return response.headers().values(name)
    }

    /**
     * Check if response has specific header
     */
    fun hasHeader(response: Response<*>, name: String): Boolean {
        return response.headers().values(name).isNotEmpty()
    }

    /**
     * Create JSON request body
     */
    fun createJsonBody(json: String): RequestBody {
        return RequestBody.create(ContentType.JSON, json)
    }

    /**
     * Create form request body
     */
    fun createFormBody(formData: String): RequestBody {
        return RequestBody.create(ContentType.FORM, formData)
    }

    /**
     * Create text request body
     */
    fun createTextBody(text: String): RequestBody {
        return RequestBody.create(ContentType.TEXT, text)
    }

    /**
     * Parse response body as string
     */
    fun parseResponseBody(responseBody: ResponseBody?): String? {
        return try {
            responseBody?.string()
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Build query string from map
     */
    fun buildQueryString(params: Map<String, Any?>): String {
        return params.entries
            .filter { it.value != null }
            .joinToString("&") { "${it.key}=${it.value}" }
    }

    /**
     * Build URL with query parameters
     */
    fun buildUrl(
        baseUrl: String,
        endpoint: String,
        params: Map<String, Any?> = emptyMap()
    ): String {
        val queryString = buildQueryString(params)
        return if (queryString.isNotEmpty()) {
            "$baseUrl$endpoint?$queryString"
        } else {
            "$baseUrl$endpoint"
        }
    }

    /**
     * Check if URL is valid
     */
    fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Extract domain from URL
     */
    fun extractDomain(url: String): String? {
        return try {
            URL(url).host
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extract path from URL
     */
    fun extractPath(url: String): String? {
        return try {
            URL(url).path
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Add query parameter to existing URL
     */
    fun addQueryParameter(url: String, key: String, value: String): String {
        val separator = if (url.contains("?")) "&" else "?"
        return "$url$separator$key=$value"
    }

    /**
     * Remove query parameter from URL
     */
    fun removeQueryParameter(url: String, key: String): String {
        val regex = Regex("([?&])$key=[^&]*&?")
        return url.replace(regex, "").removeSuffix("&").removeSuffix("?")
    }
}
