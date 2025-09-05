/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Forgot password screen for email-based password recovery
 */

package com.iiwa.authorization.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.iiwa.authorization.viewmodels.ForgotPasswordUiState
import com.iiwa.authorization.viewmodels.ForgotPasswordViewModel
import com.iiwa.components.NetworkAlertDialog
import com.iiwa.ui.theme.Dimens

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.dp24),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(Dimens.dp48)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(Dimens.dp24)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(Dimens.dp32))

        // Form State
        forgotPasswordForm(
            uiState = uiState,
            onEmailChange = { viewModel.updateEmail(it) },
            onSubmit = { viewModel.submitForgotPassword() }
        )
    }

    // Navigate to OTP screen when email is sent successfully
    LaunchedEffect(uiState.isEmailSent) {
        if (uiState.isEmailSent) {
            onNavigateToOtp(uiState.email)
        }
    }

    // Network Alert Dialog
    NetworkAlertDialog(
        showDialog = uiState.showNetworkDialog,
        onDismiss = { viewModel.hideNetworkDialog() },
        onRetry = { viewModel.retrySubmit() },
        onSettings = {
            // Open device settings
            // Note: In a real app, you'd need to handle this properly
            // val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            viewModel.hideNetworkDialog()
        }
    )
}

@Composable
private fun forgotPasswordForm(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Lock Icon
        Icon(
            Icons.Default.Lock,
            contentDescription = "Forgot Password",
            modifier = Modifier.size(Dimens.dp80),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Dimens.dp24))

        // Title
        Text(
            text = stringResource(R.string.forgot_password_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.dp16))

        // Subtitle
        Text(
            text = stringResource(R.string.forgot_password_subtitle),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(Dimens.dp48))

        // Email Field
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = stringResource(R.string.email)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            isError = uiState.emailError != null,
            supportingText = {
                if (uiState.emailError != null) {
                    Text(
                        text = uiState.emailError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.dp32))

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
                    modifier = Modifier.padding(Dimens.dp16),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(Dimens.dp16))
        }

        // Submit Button
        Button(
            onClick = onSubmit,
            enabled = !uiState.isLoading && uiState.email.isNotEmpty() && uiState.emailError == null,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp56)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.dp24),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.submit),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
