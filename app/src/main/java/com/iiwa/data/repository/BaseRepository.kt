///**
// * author - gwl
// * create date - 1 Sept 2025
// * purpose - Base repository class demonstrating proper RemoteData usage in repository pattern
// */
//
//package com.iiwa.data.repository
//
//import com.iiwa.data.remote.RemoteData
//import com.iiwa.utils.Result
//import retrofit2.Response
//
///**
// * Base repository class that provides common functionality for all repositories
// * Demonstrates proper usage of RemoteData in repository pattern
// */
//abstract class BaseRepository(
//    protected val remoteData: RemoteData
//) {
//
//    /**
//     * Execute a simple API call without additional processing
//     * Perfect for straightforward GET/POST operations
//     */
//    protected suspend fun <T> executeApiCall(
//        apiCall: suspend () -> Response<T>
//    ): Result<T> {
//        return remoteData.executeCall(apiCall)
//    }
//
//    /**
//     * Execute API call with custom result processing
//     * Useful when you need to transform or validate the response
//     */
//    protected suspend fun <T, R> executeApiCallWithTransform(
//        apiCall: suspend () -> Response<T>,
//        transform: (T) -> R
//    ): Result<R> {
//        return when (val result = remoteData.executeCall(apiCall)) {
//            is Result.Success -> {
//                try {
//                    val transformedData = transform(result.data)
//                    Result.Success(transformedData)
//                } catch (e: Exception) {
//                    Result.Error("Data transformation failed: ${e.message}")
//                }
//            }
//
//            is Result.Error -> result
//            is Result.Loading -> result
//        }
//    }
//
//    /**
//     * Execute API call with validation
//     * Ensures data meets business requirements before returning success
//     */
//    protected suspend fun <T> executeApiCallWithValidation(
//        apiCall: suspend () -> Response<T>,
//        validator: (T) -> String?
//    ): Result<T> {
//        return when (val result = remoteData.executeCall(apiCall)) {
//            is Result.Success -> {
//                val validationError = validator(result.data)
//                if (validationError != null) {
//                    Result.Error(validationError)
//                } else {
//                    result
//                }
//            }
//
//            is Result.Error -> result
//            is Result.Loading -> result
//        }
//    }
//
//    /**
//     * Execute API call with side effects (like saving to local storage)
//     * Perfect for operations that need to persist data locally after success
//     */
//    protected suspend fun <T> executeApiCallWithSideEffect(
//        apiCall: suspend () -> Response<T>,
//        onSuccess: suspend (T) -> Unit
//    ): Result<T> {
//        return when (val result = remoteData.executeCall(apiCall)) {
//            is Result.Success -> {
//                try {
//                    onSuccess(result.data)
//                    result
//                } catch (e: Exception) {
//                    Result.Error("Side effect failed: ${e.message}")
//                }
//            }
//
//            is Result.Error -> result
//            is Result.Loading -> result
//        }
//    }
//}
