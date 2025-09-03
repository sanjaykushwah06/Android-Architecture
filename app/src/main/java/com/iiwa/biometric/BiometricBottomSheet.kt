/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Biometric authentication bottom sheet with fingerprint UI and animated states
 */

package com.iiwa.biometric

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iiwa.ui.theme.Dimens
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onBiometricResult: (Boolean, String?) -> Unit,
    onStartAuthentication: () -> Unit,
    authenticationSuccess: Boolean? = null,
    authenticationError: String? = null,
    availableBiometricTypes: List<String> = listOf("fingerprint"),
    selectedBiometricType: String = "auto"
) {
    var authState by remember { mutableStateOf(BiometricAuthState.WAITING) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Handle external authentication result
    LaunchedEffect(authenticationSuccess, authenticationError) {
        when {
            authenticationSuccess == true -> {
                authState = BiometricAuthState.SUCCESS
                errorMessage = null
            }
            authenticationError != null -> {
                authState = BiometricAuthState.ERROR
                errorMessage = authenticationError
            }
        }
    }

    // Start authentication when sheet becomes visible
    LaunchedEffect(isVisible) {
        if (isVisible) {
            authState = BiometricAuthState.WAITING
            errorMessage = null
            delay(1200) // Give user time to see the waiting state
            authState = BiometricAuthState.SCANNING
            delay(500) // Brief pause before triggering system auth
            onStartAuthentication()
        }
    }

    // Handle authentication result updates
    LaunchedEffect(authState) {
        when (authState) {
            BiometricAuthState.SUCCESS -> {
                delay(1500) // Show success state briefly
                onBiometricResult(true, null)
                onDismiss()
            }

            BiometricAuthState.ERROR -> {
                // Keep error state visible for user interaction
                // Let user decide whether to retry or cancel
            }

            else -> {}
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            dragHandle = null
        ) {
            BiometricContent(
                authState = authState,
                errorMessage = errorMessage,
                onCancel = onDismiss,
                onRetry = {
                    authState = BiometricAuthState.SCANNING
                    onStartAuthentication()
                },
                availableBiometricTypes = availableBiometricTypes,
                selectedBiometricType = selectedBiometricType
            )
        }
    }

    // Expose functions to update state from parent
    LaunchedEffect(Unit) {
        // This could be improved with a more robust state management approach
    }
}

@Composable
private fun BiometricContent(
    authState: BiometricAuthState,
    errorMessage: String?,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    availableBiometricTypes: List<String> = listOf("fingerprint"),
    selectedBiometricType: String = "auto"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens._32dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dynamic Biometric Icon with Animation
        AnimatedBiometricIcon(
            authState = authState, 
            availableBiometricTypes = availableBiometricTypes,
            selectedBiometricType = selectedBiometricType
        )

        Spacer(modifier = Modifier.height(Dimens._24dp))

        // Dynamic Title based on selected biometric type
        val titleText = when (authState) {
            BiometricAuthState.WAITING -> when (selectedBiometricType) {
                "face" -> "Look at the camera"
                "fingerprint" -> "Touch sensor"
                else -> when {
                    availableBiometricTypes.contains("face") && availableBiometricTypes.contains("fingerprint") -> 
                        "Look at camera or touch sensor"
                    availableBiometricTypes.contains("face") -> 
                        "Look at the camera"
                    else -> 
                        "Touch sensor"
                }
            }
            BiometricAuthState.SCANNING -> when (selectedBiometricType) {
                "face" -> "Scanning face..."
                "fingerprint" -> "Scanning fingerprint..."
                else -> "Scanning..."
            }
            BiometricAuthState.SUCCESS -> "Authentication Successful"
            BiometricAuthState.ERROR -> "Authentication Failed"
        }
        
        Text(
            text = titleText,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens._16dp))

        // Message
        Text(
            text = when (authState) {
                BiometricAuthState.WAITING -> "Place your finger on the sensor to authenticate"
                BiometricAuthState.SCANNING -> "Keep your finger on the sensor"
                BiometricAuthState.SUCCESS -> "You have been successfully authenticated"
                BiometricAuthState.ERROR -> errorMessage
                    ?: "Authentication failed. Please try again."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(Dimens._32dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (authState == BiometricAuthState.ERROR) {
                TextButton(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Try Again")
                }
            }

            if (authState != BiometricAuthState.SUCCESS) {
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens._16dp))
    }
}

@Composable
private fun AnimatedBiometricIcon(
    authState: BiometricAuthState,
    availableBiometricTypes: List<String> = listOf("fingerprint"),
    selectedBiometricType: String = "auto"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fingerprint_animation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (authState == BiometricAuthState.SCANNING) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (authState == BiometricAuthState.SCANNING) 0.6f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val color = when (authState) {
        BiometricAuthState.WAITING -> MaterialTheme.colorScheme.primary
        BiometricAuthState.SCANNING -> MaterialTheme.colorScheme.primary
        BiometricAuthState.SUCCESS -> Color(0xFF4CAF50)
        BiometricAuthState.ERROR -> MaterialTheme.colorScheme.error
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(
                color = color.copy(alpha = 0.1f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when (authState) {
            BiometricAuthState.WAITING, BiometricAuthState.SCANNING -> {
                when (selectedBiometricType) {
                    "face" -> {
                        FaceIcon(
                            modifier = Modifier
                                .size(60.dp)
                                .scale(scale)
                                .alpha(alpha),
                            size = 60.dp,
                            color = color
                        )
                    }
                    "fingerprint" -> {
                        FingerprintIcon(
                            modifier = Modifier
                                .size(60.dp)
                                .scale(scale)
                                .alpha(alpha),
                            size = 60.dp,
                            color = color
                        )
                    }
                    else -> {
                        // Auto mode - show based on available types
                        when {
                            availableBiometricTypes.contains("face") && availableBiometricTypes.contains("fingerprint") -> {
                                // Show both icons side by side
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FaceIcon(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .scale(scale)
                                            .alpha(alpha),
                                        size = 40.dp,
                                        color = color
                                    )
                                    FingerprintIcon(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .scale(scale)
                                            .alpha(alpha),
                                        size = 40.dp,
                                        color = color
                                    )
                                }
                            }
                            availableBiometricTypes.contains("face") -> {
                                FaceIcon(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .scale(scale)
                                        .alpha(alpha),
                                    size = 60.dp,
                                    color = color
                                )
                            }
                            else -> {
                                FingerprintIcon(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .scale(scale)
                                        .alpha(alpha),
                                    size = 60.dp,
                                    color = color
                                )
                            }
                        }
                    }
                }
            }
            BiometricAuthState.SUCCESS -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale)
                        .alpha(alpha),
                    tint = color
                )
            }
            BiometricAuthState.ERROR -> {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale)
                        .alpha(alpha),
                    tint = color
                )
            }
        }
    }
}

enum class BiometricAuthState {
    WAITING,
    SCANNING,
    SUCCESS,
    ERROR
}

// Helper function to update the bottom sheet state from outside
@Composable
fun rememberBiometricBottomSheetState() = remember { mutableStateOf(false) }
