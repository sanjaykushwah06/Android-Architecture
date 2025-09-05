/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Helper functions for specific dialog types using generic alert dialog system
 */

package com.iiwa.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.iiwa.R
import com.iiwa.ui.theme.Dimens

/**
 * Network Alert Dialog - Shows when there's no internet connection
 */
@Composable
fun NetworkAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onSettings: () -> Unit
) {
    GenericAlertDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = stringResource(R.string.no_internet_title),
        message = stringResource(R.string.no_internet_message),
        icon = Icons.Default.Settings,
        iconTint = MaterialTheme.colorScheme.error,
        confirmButtonText = stringResource(R.string.try_again),
        confirmButtonAction = onRetry,
        confirmButtonStyle = AlertButtonStyle.PRIMARY,
        dismissButtonText = stringResource(R.string.open_settings),
        dismissButtonAction = onSettings,
        dismissButtonStyle = AlertButtonStyle.TEXT,
        content = {
            Spacer(modifier = Modifier.height(Dimens.dp8))
            Text(
                text = stringResource(R.string.no_internet_subtitle),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

/**
 * Notification Permission Dialog - Requests notification permission
 */
@Composable
fun NotificationPermissionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    GenericAlertDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = "Enable Notifications",
        message = "Stay updated with important information and updates from Iwaa app.",
        icon = Icons.Default.Notifications,
        iconTint = MaterialTheme.colorScheme.primary,
        confirmButtonText = "Allow",
        confirmButtonAction = onConfirm,
        confirmButtonStyle = AlertButtonStyle.PRIMARY,
        dismissButtonText = "Not Now",
        dismissButtonAction = onDismiss,
        dismissButtonStyle = AlertButtonStyle.TEXT,
        content = {
            Spacer(modifier = Modifier.height(Dimens.dp16))
            Text(
                text = "You can disable notifications anytime in your device settings.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    )
}

/**
 * Confirmation Dialog - Generic confirmation dialog
 */
@Composable
fun ConfirmDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String = "Confirm",
    message: String = "Do you want to proceed?",
    confirmText: String = "Yes",
    dismissText: String = "Cancel",
    isDestructive: Boolean = false
) {
    GenericAlertDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = title,
        message = message,
        icon = if (isDestructive) Icons.Default.Warning else null,
        iconTint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        confirmButtonText = confirmText,
        confirmButtonAction = onConfirm,
        confirmButtonStyle = if (isDestructive) AlertButtonStyle.ERROR else AlertButtonStyle.PRIMARY,
        dismissButtonText = dismissText,
        dismissButtonAction = onDismiss,
        dismissButtonStyle = AlertButtonStyle.TEXT
    )
}

/**
 * Error Dialog - Shows error messages
 */
@Composable
fun ErrorDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String = "Error",
    message: String,
    buttonText: String = "OK"
) {
    GenericAlertDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = title,
        message = message,
        icon = Icons.Default.Warning,
        iconTint = MaterialTheme.colorScheme.error,
        confirmButtonText = buttonText,
        confirmButtonAction = onDismiss,
        confirmButtonStyle = AlertButtonStyle.ERROR
    )
}

/**
 * Success Dialog - Shows success messages
 */
@Composable
fun SuccessDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String = "Success",
    message: String,
    buttonText: String = "OK"
) {
    GenericAlertDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = title,
        message = message,
        confirmButtonText = buttonText,
        confirmButtonAction = onDismiss,
        confirmButtonStyle = AlertButtonStyle.PRIMARY
    )
}
