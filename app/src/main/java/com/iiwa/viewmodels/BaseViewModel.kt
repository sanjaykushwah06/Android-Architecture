package com.iiwa.viewmodels

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iiwa.R
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Base ViewModel class providing common functionality for all ViewModels
 * Contains shared validation methods and common UI state management patterns
 * 
 * @author GWL Team
 * @date 2025-01-09
 */
abstract class BaseViewModel<T : Any>(
    protected val context: Context
) : ViewModel() {

    /**
     * Common UI state properties that most ViewModels need
     */
    data class BaseUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val showNetworkDialog: Boolean = false
    )

    /**
     * Abstract method to get the current UI state
     * Each ViewModel should implement this to return their specific state
     */
    abstract val uiState: StateFlow<T>

    /**
     * Common validation methods
     */
    
    /**
     * Validates email format
     * @param email Email string to validate
     * @return Error message if invalid, null if valid
     */
    protected open fun validateEmail(email: String): String? {
        return when {
            email.trim().isEmpty() -> context.getString(R.string.email_required)
            !isValidEmail(email) -> context.getString(R.string.email_invalid_format)
            else -> null
        }
    }

    /**
     * Email validation helper method that works in both Android and unit test environments
     * @param email Email string to validate
     * @return true if email is valid, false otherwise
     */
    private fun isValidEmail(email: String): Boolean {
        return try {
            // Try to use Android Patterns first (works in Android environment)
            if (Patterns.EMAIL_ADDRESS != null) {
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
            } else {
                // Fallback pattern for unit tests (when Patterns.EMAIL_ADDRESS is null)
                // More strict pattern that validates proper domain format
                val emailPattern = Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9][A-Za-z0-9.-]*[A-Za-z0-9]\\.[A-Za-z]{2,}$"
                )
                emailPattern.matcher(email).matches()
            }
        } catch (e: PatternSyntaxException) {
            android.util.Log.w("BaseViewModel", "Pattern syntax exception in email validation", e)
            // Fallback pattern if any exception occurs
            val emailPattern = Pattern.compile(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9][A-Za-z0-9.-]*[A-Za-z0-9]\\.[A-Za-z]{2,}$"
            )
            emailPattern.matcher(email).matches()
        }
    }

    /**
     * Validates password with basic requirements
     * @param password Password string to validate
     * @return Error message if invalid, null if valid
     */
    protected open fun validatePassword(password: String): String? {
        return when {
            password.trim().isEmpty() -> context.getString(R.string.password_required)
            password.length < 6 -> context.getString(R.string.password_min_length)
            else -> null
        }
    }

    /**
     * Validates password with advanced requirements (uppercase, digit, special char)
     * @param password Password string to validate
     * @return Error message if invalid, null if valid
     */
    protected fun validatePasswordAdvanced(password: String): String? {
        return when {
            password.trim().isEmpty() -> context.getString(R.string.password_required)
            password.length < 6 -> context.getString(R.string.password_min_length)
            !password.any { it.isUpperCase() } -> context.getString(R.string.password_uppercase_required)
            !password.any { it.isDigit() } -> context.getString(R.string.password_number_required)
            !password.any { !it.isLetterOrDigit() } -> context.getString(R.string.password_special_char_required)
            else -> null
        }
    }

    /**
     * Validates password confirmation
     * @param confirmPassword Confirmation password string
     * @param originalPassword Original password string
     * @return Error message if invalid, null if valid
     */
    protected open fun validateConfirmPassword(confirmPassword: String, originalPassword: String): String? {
        return when {
            confirmPassword.trim().isEmpty() -> context.getString(R.string.confirm_password_required)
            confirmPassword != originalPassword -> context.getString(R.string.passwords_do_not_match)
            else -> null
        }
    }

    /**
     * Common network error handling
     * @param errorMessage Error message to check
     * @return True if it's a network-related error
     */
    protected fun isNetworkError(errorMessage: String): Boolean {
        return errorMessage.contains(context.getString(R.string.no_internet_connection), ignoreCase = true) ||
                errorMessage.contains(context.getString(R.string.request_timeout), ignoreCase = true) ||
                errorMessage.contains(context.getString(R.string.network_error_generic), ignoreCase = true) ||
                errorMessage.contains(context.getString(R.string.connection_failed), ignoreCase = true)
    }

    /**
     * Common coroutine scope for ViewModel operations
     * Provides access to viewModelScope for child classes
     */
    protected fun launchCoroutine(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }
}
