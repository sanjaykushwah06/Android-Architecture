/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Login view model for handling authentication logic and biometric setup
 */

package com.iiwa.authorization.viewmodels

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.iiwa.authorization.handlers.BiometricAuthHandler
import com.iiwa.authorization.repository.UserRepository
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
    private val biometricAuthHandler: BiometricAuthHandler,
    private val userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : com.iiwa.viewmodels.BaseViewModel<LoginUiState>(context) {

    private val _uiState = MutableStateFlow(LoginUiState())
    override val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Detect available biometric types on initialization
        _uiState.value = _uiState.value.copy(
            availableBiometricTypes = biometricAuthHandler.getAvailableBiometricTypes()
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
        biometricAuthHandler.handleBiometricLogin(_uiState)
    }

    fun enableBiometric() {
        biometricAuthHandler.enableBiometric(_uiState)
    }

    fun dismissBiometricSetupDialog() {
        biometricAuthHandler.dismissBiometricSetupDialog(_uiState)
    }

    fun dismissBiometricBottomSheet() {
        biometricAuthHandler.dismissBiometricBottomSheet(_uiState)
    }

    fun dismissBiometricChoiceDialog() {
        biometricAuthHandler.dismissBiometricChoiceDialog(_uiState)
    }

    fun selectFingerprintAuth() {
        biometricAuthHandler.selectFingerprintAuth(_uiState)
    }

    fun selectFaceAuth() {
        biometricAuthHandler.selectFaceAuth(_uiState)
    }

    fun dismissFaceAuthInfoDialog() {
        biometricAuthHandler.dismissFaceAuthInfoDialog(_uiState)
    }

    fun switchToFingerprintFromFaceInfo() {
        biometricAuthHandler.switchToFingerprintFromFaceInfo(_uiState)
    }

    fun startBiometricAuthentication(activity: FragmentActivity) {
        biometricAuthHandler.startBiometricAuthentication(
            activity, 
            _uiState, 
            viewModelScope
        ) {
            // On success callback
            _uiState.value = _uiState.value.copy(isLoginSuccessful = true)
        }
    }



    fun onBiometricResult(success: Boolean, errorMessage: String?) {
        biometricAuthHandler.onBiometricResult(success, errorMessage, _uiState) {
            // On success callback - complete login after delay
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500) // Let user see success state
                _uiState.value = _uiState.value.copy(isLoginSuccessful = true)
            }
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
