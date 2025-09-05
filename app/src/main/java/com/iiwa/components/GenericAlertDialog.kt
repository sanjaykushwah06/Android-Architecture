/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Generic reusable alert dialog component with customizable buttons and content
 */

package com.iiwa.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iiwa.ui.theme.Dimens

/**
 * Generic Alert Dialog that can be reused across the app
 */
@Composable
fun GenericAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    confirmButtonText: String = "OK",
    confirmButtonAction: (() -> Unit)? = null,
    dismissButtonText: String? = null,
    dismissButtonAction: (() -> Unit)? = null,
    confirmButtonStyle: AlertButtonStyle = AlertButtonStyle.PRIMARY,
    dismissButtonStyle: AlertButtonStyle = AlertButtonStyle.TEXT,
    isDismissible: Boolean = true,
    content: @Composable (() -> Unit)? = null
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = (if (isDismissible) onDismiss else { onDismiss }) as () -> Unit,
            icon = icon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(48.dp)
                    )
                }
            },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.dp8),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Default message
                    message?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                        )
                    }
                    
                    // Custom content
                    content?.invoke()
                }
            },
            confirmButton = {
                AlertButton(
                    text = confirmButtonText,
                    onClick = confirmButtonAction ?: onDismiss,
                    style = confirmButtonStyle
                )
            },
            dismissButton = if (dismissButtonText != null) {
                {
                    AlertButton(
                        text = dismissButtonText,
                        onClick = dismissButtonAction ?: onDismiss,
                        style = dismissButtonStyle
                    )
                }
            } else null,
            modifier = Modifier.padding(Dimens.dp16)
        )
        
        // Additional button (for 3-button dialogs like NetworkAlertDialog)
        // Note: AlertDialog only supports 2 buttons, so this would need to be handled in the content
        // or by creating a custom dialog layout for 3+ buttons
    }
}

/**
 * Reusable button component for alert dialogs
 */
@Composable
private fun AlertButton(
    text: String,
    onClick: () -> Unit,
    style: AlertButtonStyle,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    when (style) {
        AlertButtonStyle.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = modifier
            ) {
                Text(text)
            }
        }
        AlertButtonStyle.SECONDARY -> {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = modifier
            ) {
                Text(text)
            }
        }
        AlertButtonStyle.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = modifier
            ) {
                Text(text)
            }
        }
        AlertButtonStyle.ERROR -> {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = modifier
            ) {
                Text(text)
            }
        }
    }
}

/**
 * Different button styles for the alert dialog
 */
enum class AlertButtonStyle {
    PRIMARY,    // Filled button with primary color
    SECONDARY,  // Filled button with secondary color
    TEXT,       // Text button
    ERROR       // Filled button with error color
}
