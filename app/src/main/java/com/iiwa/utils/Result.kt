/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Result wrapper class for handling success and error states
 */

package com.iiwa.utils

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw IllegalStateException(message ?: "Error occurred")
        is Loading -> throw IllegalStateException("Result is still loading")
    }
    
    fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    fun onError(action: (String) -> Unit): Result<T> {
        if (this is Error) action(message)
        return this
    }
    
    fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}
