/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Forgot password view model for handling password recovery flow
 */

package com.iiwa.authorization.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.iiwa.R
import com.iiwa.authorization.repository.UserRepository
import com.iiwa.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val userRepository: UserRepository
) : com.iiwa.viewmodels.BaseViewModel<ForgotPasswordUiState>(context) {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    override val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun submitForgotPassword() {
        if (!isFormValid()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            when (val result = userRepository.forgotPassword(_uiState.value.email)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmailSent = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                    // Check if it's a network error to show network dialog
                    if (result.message.contains(context.getString(R.string.no_internet_connection)) ||
                        result.message.contains(context.getString(R.string.request_timeout)) ||
                        result.message.contains(context.getString(R.string.network_error_generic))) {
                        _uiState.value = _uiState.value.copy(showNetworkDialog = true)
                    }
                }

                else -> {}
            }
        }
    }

    fun resendEmail() {
        _uiState.value = _uiState.value.copy(
            isEmailSent = false,
            email = "",
            emailError = null
        )
    }

    fun hideNetworkDialog() {
        _uiState.value = _uiState.value.copy(showNetworkDialog = false)
    }

    fun retrySubmit() {
        hideNetworkDialog()
        submitForgotPassword()
    }

    private fun isFormValid(): Boolean {
        val emailError = validateEmail(_uiState.value.email)
        
        _uiState.value = _uiState.value.copy(emailError = emailError)
        
        return emailError == null
    }

    override fun validateEmail(email: String): String? {
        return super.validateEmail(email)
    }
}

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val isEmailSent: Boolean = false,
    val errorMessage: String? = null,
    val showNetworkDialog: Boolean = false
)
