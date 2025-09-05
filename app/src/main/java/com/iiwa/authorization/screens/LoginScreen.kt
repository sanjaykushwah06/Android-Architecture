/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Login screen with email/password authentication and biometric login support
 */

package com.iiwa.authorization.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.iiwa.R
import com.iiwa.authorization.viewmodels.LoginViewModel
import com.iiwa.biometric.BiometricBottomSheet
import com.iiwa.biometric.BiometricChoiceDialog
import com.iiwa.biometric.BiometricSetupDialog
import com.iiwa.biometric.FaceAuthInfoDialog
import com.iiwa.components.NetworkAlertDialog
import com.iiwa.ui.theme.Dimens

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.dp24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo/Title
        Text(
            text = stringResource(R.string.welcome_back),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.dp8))

        Text(
            text = stringResource(R.string.sign_in_to_continue),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.dp48))

        // Email Field
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text(stringResource(R.string.email)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = stringResource(R.string.email)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
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

        Spacer(modifier = Modifier.height(Dimens.dp16))

        // Password Field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = stringResource(R.string.password)
                )
            },
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        if (uiState.isPasswordVisible) Icons.Outlined.Lock else Icons.Outlined.Lock,
                        contentDescription = if (uiState.isPasswordVisible) 
                            stringResource(R.string.hide_password) 
                        else stringResource(R.string.show_password)
                    )
                }
            },
            visualTransformation = if (uiState.isPasswordVisible) 
                VisualTransformation.None 
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isError = uiState.passwordError != null,
            supportingText = {
                if (uiState.passwordError != null) {
                    Text(
                        text = uiState.passwordError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.dp8))

        // Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onNavigateToForgotPassword,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                Text(
                    text = stringResource(R.string.forgot_password),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.dp24))

        // Login Button
        Button(
            onClick = { viewModel.login() },
            enabled = !uiState.isLoading && uiState.email.isNotEmpty() && uiState.password.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp56),
            interactionSource = remember { MutableInteractionSource() }
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.dp24),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.sign_in),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.dp24))

        // Biometric Login Button
        OutlinedButton(
            onClick = { viewModel.biometricLogin() },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp56),
            interactionSource = remember { MutableInteractionSource() }
        ) {
            Icon(
                Icons.Default.ThumbUp,
                contentDescription = "Biometric",
                modifier = Modifier.size(Dimens.dp20)
            )
            Spacer(modifier = Modifier.width(Dimens.dp8))
            Text(
                text = stringResource(R.string.sign_in_with_biometric),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(Dimens.dp32))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = Dimens.dp16),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(Dimens.dp24))

        // Sign Up Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dont_have_account),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = onNavigateToSignup,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Error Message
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(Dimens.dp16))
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
        }
    }

    // Network Alert Dialog
    NetworkAlertDialog(
        showDialog = uiState.showNetworkDialog,
        onDismiss = { viewModel.hideNetworkDialog() },
        onRetry = { viewModel.retryLogin() },
        onSettings = {
            // Open device settings
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            context.startActivity(intent)
            viewModel.hideNetworkDialog()
        }
    )

    // Biometric Setup Dialog
    BiometricSetupDialog(
        showDialog = uiState.showBiometricSetupDialog,
        onDismiss = { viewModel.dismissBiometricSetupDialog() },
        onEnable = { viewModel.enableBiometric() },
        availableBiometricTypes = uiState.availableBiometricTypes
    )

    // Biometric Choice Dialog
    BiometricChoiceDialog(
        showDialog = uiState.showBiometricChoiceDialog,
        onDismiss = { viewModel.dismissBiometricChoiceDialog() },
        onFingerprintChosen = { viewModel.selectFingerprintAuth() },
        onFaceChosen = { viewModel.selectFaceAuth() },
        availableBiometricTypes = uiState.availableBiometricTypes
    )
    
    // Face Authentication Info Dialog
    FaceAuthInfoDialog(
        showDialog = uiState.showFaceAuthInfoDialog,
        onDismiss = { viewModel.dismissFaceAuthInfoDialog() },
        onSwitchToFingerprint = { viewModel.switchToFingerprintFromFaceInfo() }
    )

    // Biometric Bottom Sheet
    BiometricBottomSheet(
        isVisible = uiState.showBiometricBottomSheet,
        onDismiss = { viewModel.dismissBiometricBottomSheet() },
        onBiometricResult = { success, error -> viewModel.onBiometricResult(success, error) },
        onStartAuthentication = {
            // Start real biometric authentication
            viewModel.startBiometricAuthentication(context as FragmentActivity)
        },
        authenticationSuccess = uiState.biometricAuthSuccess,
        authenticationError = uiState.biometricAuthError,
        availableBiometricTypes = uiState.availableBiometricTypes,
        selectedBiometricType = uiState.selectedBiometricType
    )
}
