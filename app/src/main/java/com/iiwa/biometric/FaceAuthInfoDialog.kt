/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Information dialog explaining face authentication limitations on Android devices
 */

package com.iiwa.biometric

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.iiwa.R
import com.iiwa.components.AlertButtonStyle
import com.iiwa.components.GenericAlertDialog
import com.iiwa.ui.theme.Dimens

@Composable
fun FaceAuthInfoDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSwitchToFingerprint: () -> Unit
) {
    GenericAlertDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = stringResource(R.string.auth_face_title),
        message = stringResource(R.string.biometric_face_not_supported),
        confirmButtonText = stringResource(R.string.biometric_fingerprint_option_title),
        confirmButtonAction = {
            onSwitchToFingerprint()
            onDismiss()
        },
        confirmButtonStyle = AlertButtonStyle.PRIMARY,
        dismissButtonText = stringResource(R.string.cancel),
        dismissButtonAction = onDismiss,
        dismissButtonStyle = AlertButtonStyle.TEXT,
        content = {
            Column {
                FaceIcon(
                    size = Dimens.dp48,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(Dimens.dp8))

                Text(
                    text = "Most Android devices don't have dedicated " +
                            "face authentication hardware like iPhones. " +
                            "The system may use the front camera with less secure recognition.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Dimens.dp8))

                Text(
                    text = "For better security, we recommend using fingerprint authentication.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}
