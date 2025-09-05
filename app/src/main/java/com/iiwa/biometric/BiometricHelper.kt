package com.iiwa.biometric

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Biometric authentication helper for managing fingerprint and biometric operations
 */
@Singleton
class BiometricHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("biometric_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> false
        }
    }

    fun isFingerprintAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Additional check for fingerprint specifically
                try {
                    val packageManager = context.packageManager
                    packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                } catch (e: SecurityException) {
                    android.util.Log.w("BiometricHelper", "Security exception checking fingerprint feature", e)
                    false
                } catch (e: IllegalStateException) {
                    android.util.Log.w("BiometricHelper", "Illegal state exception checking fingerprint feature", e)
                    false
                }
            }

            else -> false
        }
    }

    fun isFaceAuthAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)

        // First check if any biometric is available
        val biometricAvailable =
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> true
                else -> false
            }

        return if (!biometricAvailable) {
            false
        } else {
            // Check for face authentication features
            try {
                val packageManager = context.packageManager

                // Check for explicit face authentication features
                val hasFaceFeature =
                    packageManager.hasSystemFeature("android.hardware.biometrics.face") ||
                            packageManager.hasSystemFeature("com.samsung.android.bio.face") ||
                            packageManager.hasSystemFeature("com.android.face") ||
                            packageManager.hasSystemFeature("android.hardware.camera.front")

                // For Android 10+ (API 29+), face unlock is more standardized
                val hasModernFaceAuth = Build.VERSION.SDK_INT >= 29 &&
                        biometricManager.canAuthenticate(
                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                        ) == BiometricManager.BIOMETRIC_SUCCESS

                hasFaceFeature || hasModernFaceAuth
            } catch (e: SecurityException) {
                android.util.Log.w("BiometricHelper", "Security exception checking face auth", e)
                // Fallback: if fingerprint is not available but biometric is, assume face auth
                !isFingerprintAvailable() && biometricAvailable
            } catch (e: IllegalStateException) {
                android.util.Log.w("BiometricHelper", "Illegal state exception checking face auth", e)
                // Fallback: if fingerprint is not available but biometric is, assume face auth
                !isFingerprintAvailable() && biometricAvailable
            }
        }
    }

    fun getAvailableBiometricTypes(): List<String> {
        val types = mutableListOf<String>()
        if (isFingerprintAvailable()) types.add("fingerprint")
        // Only add face if it's actually supported (not just front camera)
        if (hasActualFaceAuthentication()) types.add("face")
        return types
    }

    fun hasActualFaceAuthentication(): Boolean {
        val biometricManager = BiometricManager.from(context)

        // Check for strong biometric authentication (required for face on most devices)
        val hasStrongBiometric =
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            ) == BiometricManager.BIOMETRIC_SUCCESS

        return if (!hasStrongBiometric) {
            false
        } else {
            try {
                val packageManager = context.packageManager

                // Check for explicit face authentication hardware
                packageManager.hasSystemFeature("android.hardware.biometrics.face") ||
                        packageManager.hasSystemFeature("com.samsung.android.bio.face") ||
                        packageManager.hasSystemFeature("com.android.face")
            } catch (e: SecurityException) {
                android.util.Log.w("BiometricHelper", "Security exception checking face hardware", e)
                false
            } catch (e: IllegalStateException) {
                android.util.Log.w("BiometricHelper", "Illegal state exception checking face hardware", e)
                false
            }
        }
    }

    suspend fun authenticate(activity: FragmentActivity): Boolean =
        suspendCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(context)

            val biometricPrompt = BiometricPrompt(
                activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        continuation.resume(false)
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        continuation.resume(true)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        continuation.resume(false)
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }

    fun getBiometricType(): String {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> "Available"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No Hardware"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Hardware Unavailable"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "Not Enrolled"
            else -> "Unknown"
        }
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    suspend fun authenticateWithCallback(
        activity: FragmentActivity,
        onResult: (Boolean, String?) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(
            activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Handle specific error codes to provide better UX
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            onResult(false, "Authentication canceled")
                        }

                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            onResult(false, "Too many attempts. Please try again later.")
                        }

                        else -> {
                            onResult(false, errString.toString())
                        }
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true, null)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Don't immediately fail on first attempt, let user try again
                    onResult(false, "Fingerprint not recognized. Try again.")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Iwaa Fingerprint")
            .setSubtitle("Touch the fingerprint sensor")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

//    suspend fun authenticateWithComponentActivity(
//        activity: ComponentActivity,
//        onResult: (Boolean, String?) -> Unit
//    ) {
//        // Since MainActivity now extends FragmentActivity, we can safely cast
//        if (activity is FragmentActivity) {
//            authenticateWithCallback(activity, onResult)
//        } else {
//            onResult(false, "Activity must be FragmentActivity for biometric authentication")
//        }
//    }

    // Alternative method for seamless authentication with minimal system UI
    suspend fun authenticateWithMinimalUI(
        activity: FragmentActivity,
        onResult: (Boolean, String?) -> Unit
    ) {
        authenticateWithSpecificType(activity, "auto", onResult)
    }

    // Authenticate with specific biometric type
    suspend fun authenticateWithSpecificType(
        activity: FragmentActivity,
        type: String, // "fingerprint", "face", or "auto"
        onResult: (Boolean, String?) -> Unit
    ) {
        // Special handling for face authentication
        if (type == "face" && !hasActualFaceAuthentication()) {
            onResult(false, "face_not_supported")
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(
            activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Handle specific error codes for better UX
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            onResult(false, "canceled")
                        }

                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            onResult(false, "locked_out")
                        }

                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            onResult(false, "no_biometrics")
                        }

                        else -> {
                            onResult(false, "try_again")
                        }
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true, null)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Return a specific error code for failed recognition
                    onResult(false, "not_recognized")
                }
            })

        // Create a dynamic prompt based on selected authentication type
        val (title, subtitle) = when (type) {
            "face" -> "Face Authentication" to "Look at the camera"
            "fingerprint" -> "Fingerprint Authentication" to "Touch the sensor"
            else -> {
                val availableTypes = getAvailableBiometricTypes()
                when {
                    availableTypes.contains("face") && availableTypes.contains("fingerprint") ->
                        "Biometric Authentication" to "Use face or fingerprint to sign in"

                    availableTypes.contains("face") ->
                        "Face Authentication" to "Look at the camera"

                    availableTypes.contains("fingerprint") ->
                        "Fingerprint Authentication" to "Touch the sensor"

                    else ->
                        "Biometric Authentication" to "Use your biometric to sign in"
                }
            }
        }

        // Configure authenticators based on selected type
        val authenticators = when (type) {
            "face" -> BiometricManager.Authenticators.BIOMETRIC_STRONG
            "fingerprint" -> BiometricManager.Authenticators.BIOMETRIC_WEAK
            else -> BiometricManager.Authenticators.BIOMETRIC_WEAK
        }

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(authenticators)
            .setConfirmationRequired(false) // Make it faster

        // Only add negative button for non-device-credential authenticators
        if (authenticators and BiometricManager.Authenticators.DEVICE_CREDENTIAL == 0) {
            promptInfoBuilder.setNegativeButtonText("Cancel")
        }

        val promptInfo = promptInfoBuilder.build()

        biometricPrompt.authenticate(promptInfo)
    }
}
