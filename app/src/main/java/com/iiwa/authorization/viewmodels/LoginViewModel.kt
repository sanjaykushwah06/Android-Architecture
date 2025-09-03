/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Login view model for handling authentication logic and biometric setup
 */

package com.iiwa.authorization.viewmodels

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.iiwa.R
import com.iiwa.authorization.repository.UserRepository
import com.iiwa.biometric.BiometricHelper
import com.iiwa.utils.NetworkUtils
import com.iiwa.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val biometricHelper: BiometricHelper,
    private val userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : com.iiwa.viewmodels.BaseViewModel<LoginUiState>(context) {

    private val _uiState = MutableStateFlow(LoginUiState())
    override val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Detect available biometric types on initialization
        _uiState.value = _uiState.value.copy(
            availableBiometricTypes = biometricHelper.getAvailableBiometricTypes()
        )
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password)
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun login() {
        if (!isFormValid()) return

        // Check internet connectivity before making API call
        if (!networkUtils.isInternetAvailable()) {
            _uiState.value = _uiState.value.copy(
                showNetworkDialog = true,
                errorMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = userRepository.login(
                _uiState.value.email,
                _uiState.value.password
            )) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }

                else -> {}
            }
        }
    }

    fun biometricLogin() {
        // Check if biometric is enabled by user
        if (biometricHelper.isBiometricEnabled()) {
            val availableTypes = _uiState.value.availableBiometricTypes

            // If multiple biometric types are available, show choice dialog
            if (availableTypes.size > 1) {
                _uiState.value = _uiState.value.copy(
                    showBiometricChoiceDialog = true
                )
            } else {
                // If only one type is available, proceed directly
                _uiState.value = _uiState.value.copy(
                    showBiometricBottomSheet = true,
                    biometricAuthSuccess = null,
                    biometricAuthError = null,
                    errorMessage = null,
                    selectedBiometricType = availableTypes.firstOrNull() ?: context.getString(R.string.biometric_type_auto)
                )
            }
        } else {
            // Show setup dialog first
            _uiState.value = _uiState.value.copy(showBiometricSetupDialog = true)
        }
    }

    fun enableBiometric() {
        biometricHelper.setBiometricEnabled(true)
        _uiState.value = _uiState.value.copy(
            showBiometricSetupDialog = false,
            showBiometricBottomSheet = true,
            biometricAuthSuccess = null,
            biometricAuthError = null
        )
    }

    fun dismissBiometricSetupDialog() {
        _uiState.value = _uiState.value.copy(showBiometricSetupDialog = false)
    }

    fun dismissBiometricBottomSheet() {
        _uiState.value = _uiState.value.copy(
            showBiometricBottomSheet = false,
            biometricAuthSuccess = null,
            biometricAuthError = null
        )
    }

    fun dismissBiometricChoiceDialog() {
        _uiState.value = _uiState.value.copy(showBiometricChoiceDialog = false)
    }

    fun selectFingerprintAuth() {
        _uiState.value = _uiState.value.copy(
            showBiometricChoiceDialog = false,
            showBiometricBottomSheet = true,
            selectedBiometricType = context.getString(R.string.biometric_type_fingerprint),
            biometricAuthSuccess = null,
            biometricAuthError = null,
            errorMessage = null
        )
    }

    fun selectFaceAuth() {
        // Check if face authentication is actually supported before proceeding
        if (!biometricHelper.hasActualFaceAuthentication()) {
            _uiState.value = _uiState.value.copy(
                showBiometricChoiceDialog = false,
                showFaceAuthInfoDialog = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                showBiometricChoiceDialog = false,
                showBiometricBottomSheet = true,
                selectedBiometricType = context.getString(R.string.biometric_type_face),
                biometricAuthSuccess = null,
                biometricAuthError = null,
                errorMessage = null
            )
        }
    }

    fun dismissFaceAuthInfoDialog() {
        _uiState.value = _uiState.value.copy(showFaceAuthInfoDialog = false)
    }

    fun switchToFingerprintFromFaceInfo() {
        _uiState.value = _uiState.value.copy(
            showFaceAuthInfoDialog = false,
            showBiometricBottomSheet = true,
            selectedBiometricType = context.getString(R.string.biometric_type_fingerprint),
            biometricAuthSuccess = null,
            biometricAuthError = null,
            errorMessage = null
        )
    }

    fun startBiometricAuthentication(activity: FragmentActivity) {
        viewModelScope.launch {
            // Check if biometric is available before starting
            if (!biometricHelper.isBiometricAvailable()) {
                _uiState.value = _uiState.value.copy(
                    showBiometricBottomSheet = false,
                    errorMessage = context.getString(R.string.biometric_not_available)
                )
                return@launch
            }

            // Use specific authentication type based on user selection
            val selectedType = _uiState.value.selectedBiometricType
            biometricHelper.authenticateWithSpecificType(
                activity,
                selectedType
            ) { success, errorCode ->
                handleBiometricResult(success, errorCode)
            }
        }
    }

    private fun handleBiometricResult(success: Boolean, errorCode: String?) {
        if (success) {
            onBiometricResult(true, null)
        } else {
            val errorMessage = when (errorCode) {
                "canceled" -> context.getString(R.string.biometric_auth_canceled)
                "locked_out" -> context.getString(R.string.biometric_too_many_attempts)
                "no_biometrics" -> context.getString(R.string.biometric_no_biometrics)
                "not_recognized" -> when (_uiState.value.selectedBiometricType) {
                    context.getString(R.string.biometric_type_face) -> context.getString(R.string.biometric_face_not_recognized)
                    else -> context.getString(R.string.biometric_not_recognized)
                }

                "try_again" -> context.getString(R.string.biometric_try_again)
                "face_not_supported" -> context.getString(R.string.biometric_face_not_supported)
                else -> errorCode ?: context.getString(R.string.biometric_auth_failed_generic)
            }

            // For retry-able errors, don't dismiss the bottom sheet
            if (errorCode in listOf("not_recognized", "try_again")) {
                _uiState.value = _uiState.value.copy(
                    biometricAuthSuccess = false,
                    biometricAuthError = errorMessage
                )
            } else if (errorCode == "face_not_supported") {
                // For face not supported, show info dialog
                _uiState.value = _uiState.value.copy(
                    showBiometricBottomSheet = false,
                    showFaceAuthInfoDialog = true,
                    biometricAuthSuccess = null,
                    biometricAuthError = null
                )
            } else {
                // For non-retry-able errors, dismiss and show error
                onBiometricResult(false, errorMessage)
            }
        }
    }

    fun onBiometricResult(success: Boolean, errorMessage: String?) {
        if (success) {
            _uiState.value = _uiState.value.copy(
                biometricAuthSuccess = true,
                biometricAuthError = null
            )
            // After showing success in bottom sheet, complete login
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500) // Let user see success state
                _uiState.value = _uiState.value.copy(
                    showBiometricBottomSheet = false,
                    isLoginSuccessful = true
                )
            }
        } else {
            _uiState.value = _uiState.value.copy(
                biometricAuthSuccess = false,
                biometricAuthError = errorMessage ?: context.getString(R.string.biometric_auth_failed_generic)
            )
        }
    }


    fun hideNetworkDialog() {
        _uiState.value = _uiState.value.copy(showNetworkDialog = false)
    }

    fun retryLogin() {
        hideNetworkDialog()
        login()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            // Reset UI state
            _uiState.value = LoginUiState()
        }
    }

    private fun isFormValid(): Boolean {
        val emailError = validateEmail(_uiState.value.email)
        val passwordError = validatePassword(_uiState.value.password)

        _uiState.value = _uiState.value.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    override fun validateEmail(email: String): String? {
        return super.validateEmail(email)
    }

    override fun validatePassword(password: String): String? {
        // For now, use basic validation (6 digits only as requested)
        return super.validatePassword(password)
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val errorMessage: String? = null,
    val showNetworkDialog: Boolean = false,
    val showBiometricSetupDialog: Boolean = false,
    val showBiometricBottomSheet: Boolean = false,
    val showBiometricChoiceDialog: Boolean = false,
    val showFaceAuthInfoDialog: Boolean = false,
    val biometricAuthSuccess: Boolean? = null,
    val biometricAuthError: String? = null,
    val availableBiometricTypes: List<String> = emptyList(),
    val selectedBiometricType: String = ""
)
