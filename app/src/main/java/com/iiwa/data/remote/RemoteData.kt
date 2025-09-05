/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Generic remote data processor for repository pattern with centralized error handling
 */

package com.iiwa.data.remote

import com.iiwa.data.error.NETWORK_ERROR
import com.iiwa.data.error.NO_INTERNET_CONNECTION
import com.iiwa.utils.NetworkConnectivity
import com.iiwa.utils.Result
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteData @Inject constructor(private val networkConnectivity: NetworkConnectivity) {
    
    /**
     * Process API call with comprehensive error handling
     * Returns raw response data or error codes
     */
    internal suspend fun processCall(responseCall: suspend () -> Response<*>): Any? {
        if (!networkConnectivity.isConnected()) {
            return NO_INTERNET_CONNECTION
        }
        return try {
            val response = responseCall.invoke()
            if (response.isSuccessful) {
                response.body()
            } else {
                response.code()
            }
        } catch (e: IOException) {
            android.util.Log.w("RemoteData", "IOException in API call", e)
            NETWORK_ERROR
        }
    }
    
    /**
     * Process API call and return Result wrapper (recommended for repositories)
     * Generic type-safe version that handles all error scenarios
     */
    suspend fun <T> executeCall(responseCall: suspend () -> Response<T>): Result<T> {
        if (!networkConnectivity.isConnected()) {
            return Result.Error("No internet connection available")
        }
        
        return try {
            val response = responseCall.invoke()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body")
            } else {
                // Map HTTP error codes to user-friendly messages
                val errorMessage = when (response.code()) {
                    400 -> "Invalid request data"
                    401 -> "Authentication failed"
                    403 -> "Access denied"
                    404 -> "Resource not found"
                    408 -> "Request timeout"
                    422 -> "Validation failed"
                    429 -> "Too many requests"
                    500 -> "Server error occurred"
                    502 -> "Bad gateway"
                    503 -> "Service unavailable"
                    504 -> "Gateway timeout"
                    else -> "Network error: ${response.code()}"
                }
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            android.util.Log.w("RemoteData", "IOException in API call", e)
            Result.Error("Network connection error")
        } catch (e: HttpException) {
            android.util.Log.w("RemoteData", "HttpException in API call", e)
            Result.Error("HTTP error: ${e.code()}")
        }
    }
}
