/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Dialog for user to choose between fingerprint and face authentication
 */

package com.iiwa.biometric

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.iiwa.ui.theme.Dimens

@Composable
fun BiometricChoiceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onFingerprintChosen: () -> Unit,
    onFaceChosen: () -> Unit,
    availableBiometricTypes: List<String> = emptyList()
) {
    if (showDialog && availableBiometricTypes.size > 1) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens._16dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens._24dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Choose Authentication Method",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(Dimens._8dp))
                    
                    Text(
                        text = "Select your preferred biometric authentication method",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(Dimens._24dp))
                    
                    // Authentication Options
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Dimens._12dp)
                    ) {
                        // Face Authentication Option
                        if (availableBiometricTypes.contains("face")) {
                            BiometricOptionCard(
                                icon = { 
                                    FaceIcon(
                                        size = 32.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                title = "Face Recognition",
                                description = "Look at the camera to authenticate",
                                onClick = {
                                    onFaceChosen()
                                    onDismiss()
                                }
                            )
                        }
                        
                        // Fingerprint Authentication Option
                        if (availableBiometricTypes.contains("fingerprint")) {
                            BiometricOptionCard(
                                icon = { 
                                    FingerprintIcon(
                                        size = 32.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                title = "Fingerprint",
                                description = "Touch the fingerprint sensor",
                                onClick = {
                                    onFingerprintChosen()
                                    onDismiss()
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens._16dp))
                    
                    // Cancel Button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BiometricOptionCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens._16dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            
            Spacer(modifier = Modifier.width(Dimens._16dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
