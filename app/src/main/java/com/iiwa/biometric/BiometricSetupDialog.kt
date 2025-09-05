/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Biometric setup confirmation dialog for enabling fingerprint authentication
 */

package com.iiwa.biometric

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iiwa.components.AlertButtonStyle
import com.iiwa.components.GenericAlertDialog
import com.iiwa.ui.theme.Dimens

@Composable
fun BiometricSetupDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onEnable: () -> Unit,
    availableBiometricTypes: List<String> = listOf("fingerprint")
) {
    val (title, message, description) = when {
        availableBiometricTypes.contains("face") && availableBiometricTypes.contains("fingerprint") -> 
            Triple(
                "Enable Biometric Authentication",
                "Do you want to enable face and fingerprint authentication for Iwaa app?",
                "Use face recognition or fingerprint for faster and more secure sign-in."
            )
        availableBiometricTypes.contains("face") -> 
            Triple(
                "Enable Face Authentication",
                "Do you want to enable face authentication for Iwaa app?",
                "Use face recognition for faster and more secure sign-in."
            )
        else -> 
            Triple(
                "Enable Fingerprint Authentication",
                "Do you want to enable fingerprint authentication for Iwaa app?",
                "Use your fingerprint for faster and more secure sign-in."
            )
    }

    GenericAlertDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = title,
        message = message,
        confirmButtonText = "Enable",
        confirmButtonAction = onEnable,
        confirmButtonStyle = AlertButtonStyle.PRIMARY,
        dismissButtonText = "Not Now",
        dismissButtonAction = onDismiss,
        dismissButtonStyle = AlertButtonStyle.TEXT,
        content = {
            // Show appropriate biometric icon(s)
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    availableBiometricTypes.contains("face") && availableBiometricTypes.contains("fingerprint") -> {
                        // Show both icons side by side
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FaceIcon(
                                size = 32.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            FingerprintIcon(
                                size = 32.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    availableBiometricTypes.contains("face") -> {
                        FaceIcon(
                            size = 48.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    else -> {
                        FingerprintIcon(
                            size = 48.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.dp8))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    )
}
