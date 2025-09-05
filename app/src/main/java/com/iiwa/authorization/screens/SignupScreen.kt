/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - User registration screen with form validation
 */

package com.iiwa.authorization.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iiwa.R
import com.iiwa.authorization.viewmodels.SignupUiState
import com.iiwa.authorization.viewmodels.SignupViewModel
import com.iiwa.ui.theme.Dimens

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isSignupSuccessful) {
        if (uiState.isSignupSuccessful) {
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
        // App Title
        Text(
            text = stringResource(R.string.create_account),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimens.dp8))
        
        Text(
            text = stringResource(R.string.sign_up_to_get_started),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimens.dp48))
        
        // Signup Form
        signupForm(
            uiState = uiState,
            onEmailChange = { viewModel.updateEmail(it) },
            onPasswordChange = { viewModel.updatePassword(it) },
            onConfirmPasswordChange = { viewModel.updateConfirmPassword(it) },
            onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() },
            onToggleConfirmPasswordVisibility = { viewModel.toggleConfirmPasswordVisibility() }
        )
        
        Spacer(modifier = Modifier.height(Dimens.dp24))
        
        // Signup Button
        signupButton(
            uiState = uiState,
            onSignup = { viewModel.signup() }
        )
        
        Spacer(modifier = Modifier.height(Dimens.dp24))
        
        // Login Link
        loginLink(onNavigateToLogin = onNavigateToLogin)
        
        // Error Message
        errorMessage(uiState = uiState)
    }
}

@Composable
private fun signupForm(
    uiState: SignupUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit
) {
    // Email Field
    OutlinedTextField(
        value = uiState.email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.email)) },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = stringResource(R.string.email)) },
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
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.password)) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = stringResource(R.string.password)) },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
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
            imeAction = ImeAction.Next
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
    
    Spacer(modifier = Modifier.height(Dimens.dp16))
    
    // Confirm Password Field
    OutlinedTextField(
        value = uiState.confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text(stringResource(R.string.confirm_password)) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = stringResource(R.string.confirm_password)) },
        trailingIcon = {
            IconButton(onClick = onToggleConfirmPasswordVisibility) {
                Icon(
                    if (uiState.isConfirmPasswordVisible) Icons.Outlined.Lock else Icons.Outlined.Lock,
                    contentDescription = if (uiState.isConfirmPasswordVisible) 
                        stringResource(R.string.hide_password) 
                    else stringResource(R.string.show_password)
                )
            }
        },
        visualTransformation = if (uiState.isConfirmPasswordVisible) 
            VisualTransformation.None 
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        isError = uiState.confirmPasswordError != null,
        supportingText = {
            if (uiState.confirmPasswordError != null) {
                Text(
                    text = uiState.confirmPasswordError!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun signupButton(
    uiState: SignupUiState,
    onSignup: () -> Unit
) {
    Button(
        onClick = onSignup,
        enabled = !uiState.isLoading && uiState.email.isNotEmpty() && 
                 uiState.password.isNotEmpty() && uiState.confirmPassword.isNotEmpty(),
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
                text = stringResource(R.string.sign_up),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun loginLink(
    onNavigateToLogin: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.already_have_account),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
            onClick = onNavigateToLogin,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            Text(
                text = stringResource(R.string.sign_in),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun errorMessage(
    uiState: SignupUiState
) {
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
