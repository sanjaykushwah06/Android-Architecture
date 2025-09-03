/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - OTP verification screen for email-based authentication
 */

package com.iiwa.authorization.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iiwa.R
import com.iiwa.authorization.viewmodels.OtpUiState
import com.iiwa.authorization.viewmodels.OtpViewModel
import com.iiwa.components.NetworkAlertDialog
import com.iiwa.ui.theme.Dimens
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    email: String,
    onNavigateBack: () -> Unit,
    onOtpVerified: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Set email when screen is created
    LaunchedEffect(email) {
        viewModel.setEmail(email)
    }

    // Navigate to login screen after successful OTP verification
    LaunchedEffect(uiState.isOtpVerified) {
        if (uiState.isOtpVerified) {
            // Show success message briefly before navigating to login
            delay(2000)
            onOtpVerified()
        }
    }

    // Show OTP resent message
    LaunchedEffect(uiState.showOtpResentMessage) {
        if (uiState.showOtpResentMessage) {
            snackbarHostState.showSnackbar("OTP resent successfully!")
            viewModel.hideOtpResentMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens._24dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(Dimens._48dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(Dimens._24dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(Dimens._32dp))

        if (uiState.isOtpVerified) {
            // Success State
            OtpSuccessContent()
        } else {
            // OTP Input State
            OtpInputContent(
                uiState = uiState,
                onOtpChange = { viewModel.updateOtp(it) },
                onVerifyOtp = { viewModel.verifyOtp() },
                onResendOtp = { viewModel.resendOtp() }
            )
        }
    }

    // Network Alert Dialog
    NetworkAlertDialog(
        showDialog = uiState.showNetworkDialog,
        onDismiss = { viewModel.hideNetworkDialog() },
        onRetry = { viewModel.retryVerification() },
        onSettings = {
            // Open device settings
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            // Note: In a real app, you'd need to handle this properly
            viewModel.hideNetworkDialog()
        }
    )

    // Snackbar for OTP resent message
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(Dimens._16dp)
    )
}

@Composable
private fun OtpInputContent(
    uiState: OtpUiState,
    onOtpChange: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onResendOtp: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Key Icon
        Icon(
            Icons.Default.Settings,
            contentDescription = "OTP",
            modifier = Modifier.size(Dimens._80dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Dimens._24dp))

        // Title
        Text(
            text = stringResource(R.string.otp_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens._16dp))

        // Subtitle
        Text(
            text = stringResource(R.string.otp_subtitle),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(Dimens._8dp))
        
        // Test OTP Hint - Remove this in production when implementing real OTP
        Text(
            text = "Test OTP: 1234",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(Dimens._8dp))

        // Email display
        Text(
            text = uiState.email,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(Dimens._48dp))

        // OTP Input Field
        OutlinedTextField(
            value = uiState.otp,
            onValueChange = onOtpChange,
            label = { Text(stringResource(R.string.otp_placeholder)) },
            leadingIcon = { 
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "OTP"
                ) 
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = uiState.otpError != null,
            supportingText = {
                if (uiState.otpError != null) {
                    Text(
                        text = uiState.otpError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens._32dp))

        // Error Message
        if (uiState.errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(Dimens._16dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(Dimens._16dp))
        }

        // Verify OTP Button
        Button(
            onClick = onVerifyOtp,
            enabled = !uiState.isLoading && uiState.otp.length == 4 && uiState.otpError == null,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens._56dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens._24dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.verify_otp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens._32dp))

        // Resend OTP Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.canResendOtp) {
                // Resend OTP Button
                TextButton(
                    onClick = onResendOtp,
                    enabled = !uiState.isResendingOtp
                ) {
                    if (uiState.isResendingOtp) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(Dimens._16dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(Dimens._8dp))
                    } else {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Resend OTP",
                            modifier = Modifier.size(Dimens._16dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens._8dp))
                    }
                    Text(
                        text = stringResource(R.string.resend_otp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                // Resend countdown
                Text(
                    text = stringResource(R.string.resend_otp_in, uiState.resendCountdown),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun OtpSuccessContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Success Icon
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "OTP Verified",
            modifier = Modifier.size(Dimens._80dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Dimens._24dp))

        // Success Title
        Text(
            text = stringResource(R.string.otp_verification_successful),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens._16dp))

        // Success Message
        Text(
            text = "OTP verified successfully! You will be redirected to login.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
