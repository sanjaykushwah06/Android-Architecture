/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Signup view model for handling user registration and form validation
 */

package com.iiwa.authorization.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.iiwa.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    @ApplicationContext context: Context
) : com.iiwa.viewmodels.BaseViewModel<SignupUiState>(context) {

    private val _uiState = MutableStateFlow(SignupUiState())
    override val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password),
            confirmPasswordError = validateConfirmPassword(_uiState.value.confirmPassword, password)
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validateConfirmPassword(confirmPassword, _uiState.value.password)
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    fun signup() {
        if (!isFormValid()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Simulate API call
                delay(1500)
                
                // For demo purposes, accept any valid form
                if (_uiState.value.email.isNotEmpty() && 
                    _uiState.value.password.isNotEmpty() && 
                    _uiState.value.confirmPassword.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignupSuccessful = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = context.getString(R.string.please_fill_all_fields)
                    )
                }
            } catch (e: IllegalStateException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = context.getString(R.string.signup_failed, e.message ?: "")
                )
            } catch (e: SecurityException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = context.getString(R.string.signup_failed, e.message ?: "")
                )
            }
        }
    }

    private fun isFormValid(): Boolean {
        val emailError = validateEmail(_uiState.value.email)
        val passwordError = validatePassword(_uiState.value.password)
        val confirmPasswordError = validateConfirmPassword(_uiState.value.confirmPassword, _uiState.value.password)
        
        _uiState.value = _uiState.value.copy(
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )
        
        return emailError == null && passwordError == null && confirmPasswordError == null
    }

    override fun validateEmail(email: String): String? {
        return super.validateEmail(email)
    }

    override fun validatePassword(password: String): String? {
        // Comment out advanced validation for now (as requested)
        // return super.validatePasswordAdvanced(password)
        return super.validatePassword(password)
    }

    override fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return super.validateConfirmPassword(confirmPassword, password)
    }
}

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSignupSuccessful: Boolean = false,
    val errorMessage: String? = null
)
