/**
 * author - gwl
 * create date - 3 Jan 2025
 * purpose - Handler for biometric authentication logic, extracted from LoginViewModel
 */

package com.iiwa.authorization.handlers

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.iiwa.R
import com.iiwa.authorization.viewmodels.LoginUiState
import com.iiwa.biometric.BiometricHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricAuthHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val biometricHelper: BiometricHelper
) {
    
    fun handleBiometricLogin(
        uiState: MutableStateFlow<LoginUiState>
    ) {
        // Check if biometric is enabled by user
        if (biometricHelper.isBiometricEnabled()) {
            val availableTypes = uiState.value.availableBiometricTypes

            // If multiple biometric types are available, show choice dialog
            if (availableTypes.size > 1) {
                uiState.value = uiState.value.copy(
                    showBiometricChoiceDialog = true
                )
            } else {
                // If only one type is available, proceed directly
                uiState.value = uiState.value.copy(
                    showBiometricBottomSheet = true,
                    biometricAuthSuccess = null,
                    biometricAuthError = null,
                    errorMessage = null,
                    selectedBiometricType = availableTypes.firstOrNull() 
                        ?: context.getString(R.string.biometric_type_auto)
                )
            }
        } else {
            // Show setup dialog first
            uiState.value = uiState.value.copy(showBiometricSetupDialog = true)
        }
    }

    fun enableBiometric(uiState: MutableStateFlow<LoginUiState>) {
        biometricHelper.setBiometricEnabled(true)
        uiState.value = uiState.value.copy(
            showBiometricSetupDialog = false,
            showBiometricBottomSheet = true,
            biometricAuthSuccess = null,
            biometricAuthError = null
        )
    }

    fun dismissBiometricSetupDialog(uiState: MutableStateFlow<LoginUiState>) {
        uiState.value = uiState.value.copy(showBiometricSetupDialog = false)
    }

    fun dismissBiometricBottomSheet(uiState: MutableStateFlow<LoginUiState>) {
        uiState.value = uiState.value.copy(
            showBiometricBottomSheet = false,
            biometricAuthSuccess = null,
            biometricAuthError = null
        )
    }

    fun dismissBiometricChoiceDialog(uiState: MutableStateFlow<LoginUiState>) {
        uiState.value = uiState.value.copy(showBiometricChoiceDialog = false)
    }

    fun selectFingerprintAuth(uiState: MutableStateFlow<LoginUiState>) {
        uiState.value = uiState.value.copy(
            showBiometricChoiceDialog = false,
            showBiometricBottomSheet = true,
            selectedBiometricType = context.getString(R.string.biometric_type_fingerprint),
            biometricAuthSuccess = null,
            biometricAuthError = null,
            errorMessage = null
        )
    }

    fun selectFaceAuth(uiState: MutableStateFlow<LoginUiState>) {
        // Check if face authentication is actually supported before proceeding
        if (!biometricHelper.hasActualFaceAuthentication()) {
            uiState.value = uiState.value.copy(
                showBiometricChoiceDialog = false,
                showFaceAuthInfoDialog = true
            )
        } else {
            uiState.value = uiState.value.copy(
                showBiometricChoiceDialog = false,
                showBiometricBottomSheet = true,
                selectedBiometricType = context.getString(R.string.biometric_type_face),
                biometricAuthSuccess = null,
                biometricAuthError = null,
                errorMessage = null
            )
        }
    }

    fun dismissFaceAuthInfoDialog(uiState: MutableStateFlow<LoginUiState>) {
        uiState.value = uiState.value.copy(showFaceAuthInfoDialog = false)
    }

    fun switchToFingerprintFromFaceInfo(uiState: MutableStateFlow<LoginUiState>) {
        uiState.value = uiState.value.copy(
            showFaceAuthInfoDialog = false,
            showBiometricBottomSheet = true,
            selectedBiometricType = context.getString(R.string.biometric_type_fingerprint),
            biometricAuthSuccess = null,
            biometricAuthError = null,
            errorMessage = null
        )
    }

    fun startBiometricAuthentication(
        activity: FragmentActivity,
        uiState: MutableStateFlow<LoginUiState>,
        coroutineScope: CoroutineScope,
        onSuccess: () -> Unit
    ) {
        coroutineScope.launch {
            val selectedType = uiState.value.selectedBiometricType
            val typeString = when {
                selectedType == context.getString(R.string.biometric_type_fingerprint) -> "fingerprint"
                selectedType == context.getString(R.string.biometric_type_face) -> "face"
                else -> "auto"
            }

            biometricHelper.authenticateWithSpecificType(activity, typeString) { success, errorCode ->
                handleBiometricResult(success, errorCode, uiState, onSuccess)
            }
        }
    }

    private fun handleBiometricResult(
        success: Boolean, 
        errorCode: String?, 
        uiState: MutableStateFlow<LoginUiState>,
        onSuccess: () -> Unit
    ) {
        if (success) {
            uiState.value = uiState.value.copy(
                biometricAuthSuccess = true,
                biometricAuthError = null,
                errorMessage = null,
                showBiometricBottomSheet = false
            )
            onSuccess()
        } else {
            val errorMessage = when (errorCode) {
                "canceled" -> context.getString(R.string.biometric_auth_canceled)
                "locked_out" -> context.getString(R.string.biometric_too_many_attempts)
                "no_biometrics" -> context.getString(R.string.biometric_no_biometrics)
                "not_recognized" -> when (uiState.value.selectedBiometricType) {
                    context.getString(R.string.biometric_type_face) -> 
                        context.getString(R.string.biometric_face_not_recognized)
                    else -> context.getString(R.string.biometric_not_recognized)
                }
                "try_again" -> context.getString(R.string.biometric_try_again)
                "face_not_supported" -> context.getString(R.string.biometric_face_not_supported)
                else -> errorCode ?: context.getString(R.string.biometric_auth_failed_generic)
            }

            uiState.value = uiState.value.copy(
                biometricAuthSuccess = false,
                biometricAuthError = errorMessage,
                errorMessage = null
            )
        }
    }

    fun onBiometricResult(
        success: Boolean, 
        errorMessage: String?, 
        uiState: MutableStateFlow<LoginUiState>,
        onSuccess: () -> Unit
    ) {
        if (success) {
            uiState.value = uiState.value.copy(
                biometricAuthSuccess = true,
                biometricAuthError = null,
                errorMessage = null,
                showBiometricBottomSheet = false,
                isLoginSuccessful = true
            )
            onSuccess()
        } else {
            uiState.value = uiState.value.copy(
                biometricAuthSuccess = false,
                biometricAuthError = errorMessage ?: context.getString(R.string.biometric_auth_failed_generic),
                errorMessage = null
            )
        }
    }

    fun getAvailableBiometricTypes(): List<String> {
        return biometricHelper.getAvailableBiometricTypes()
    }
}
