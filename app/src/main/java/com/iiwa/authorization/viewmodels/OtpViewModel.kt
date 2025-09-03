/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - OTP verification view model for handling one-time password validation
 */

package com.iiwa.authorization.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.iiwa.R
import com.iiwa.authorization.repository.UserRepository
import com.iiwa.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val userRepository: UserRepository
) : com.iiwa.viewmodels.BaseViewModel<OtpUiState>(context) {

    private val _uiState = MutableStateFlow(OtpUiState())
    override val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    init {
        startResendTimer()
    }

    fun updateOtp(otp: String) {
        // Only allow 4 digits
        if (otp.length <= 4 && otp.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                otp = otp,
                otpError = validateOtp(otp)
            )
        }
    }

    fun verifyOtp() {
        if (!isFormValid()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Check if OTP equals "1234" for success
            if (_uiState.value.otp == "1234") {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isOtpVerified = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = context.getString(R.string.invalid_otp_test)
                )
            }
            
            // Uncomment this when you want to use the repository API call instead
            /*
            when (val result = userRepository.verifyOtp(_uiState.value.email, _uiState.value.otp)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpVerified = true
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
            }
            */
        }
    }

    fun resendOtp() {
        if (_uiState.value.canResendOtp) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isResendingOtp = true)
                
                when (val result = userRepository.forgotPassword(_uiState.value.email)) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isResendingOtp = false,
                            showOtpResentMessage = true
                        )
                        startResendTimer()
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isResendingOtp = false,
                            errorMessage = result.message
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    fun hideNetworkDialog() {
        _uiState.value = _uiState.value.copy(showNetworkDialog = false)
    }

    fun retryVerification() {
        hideNetworkDialog()
        verifyOtp()
    }

    fun hideOtpResentMessage() {
        _uiState.value = _uiState.value.copy(showOtpResentMessage = false)
    }

    fun setEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    private fun startResendTimer() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                resendCountdown = 60,
                canResendOtp = false
            )
            
            while (_uiState.value.resendCountdown > 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    resendCountdown = _uiState.value.resendCountdown - 1
                )
            }
            
            _uiState.value = _uiState.value.copy(canResendOtp = true)
        }
    }

    private fun isFormValid(): Boolean {
        val otpError = validateOtp(_uiState.value.otp)
        
        _uiState.value = _uiState.value.copy(otpError = otpError)
        
        return otpError == null
    }

    private fun validateOtp(otp: String): String? {
        return when {
            otp.isEmpty() -> context.getString(R.string.otp_required)
            otp.length != 4 -> context.getString(R.string.otp_must_be_4_digits)
            !otp.all { it.isDigit() } -> context.getString(R.string.otp_must_be_numbers)
            else -> null
        }
    }
}

data class OtpUiState(
    val email: String = "",
    val otp: String = "",
    val otpError: String? = null,
    val isLoading: Boolean = false,
    val isOtpVerified: Boolean = false,
    val errorMessage: String? = null,
    val showNetworkDialog: Boolean = false,
    val resendCountdown: Int = 60,
    val canResendOtp: Boolean = false,
    val isResendingOtp: Boolean = false,
    val showOtpResentMessage: Boolean = false
)
