/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Navigation graph defining app routes and screen transitions
 */

package com.iiwa.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.iiwa.components.ConfirmDialog
import com.iiwa.home.screens.DetailsScreen
import com.iiwa.authorization.screens.ForgotPasswordScreen
import com.iiwa.home.screens.HomeScreen
import com.iiwa.authorization.screens.LoginScreen
import com.iiwa.authorization.screens.OtpScreen
import com.iiwa.authorization.screens.ResetPasswordScreen
import com.iiwa.authorization.screens.SignupScreen
import com.iiwa.home.viewmodels.DetailsViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        // Signup
        composable(Screen.Signup.route) {
            SignupScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToHome = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Forgot Password
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOtp = { email ->
                    navController.navigate(Screen.Otp.createRoute(email))
                }
            )
        }

        // OTP Verification
        composable(
            route = Screen.Otp.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) {
            val email = it.arguments?.getString("email") ?: ""
            OtpScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onOtpVerified = {
                    // Navigate to reset password screen after OTP verification
                    navController.navigate(Screen.ResetPassword.createRoute(email))
                }
            )
        }

        // Reset Password
        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) {
            val email = it.arguments?.getString("email") ?: ""
            ResetPasswordScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    // Navigate to login screen and clear entire navigation graph
                    // This ensures user starts fresh from login after password reset
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Details
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) {
            val vm: DetailsViewModel = hiltViewModel()
            DetailsScreen(viewModel = vm)
        }

        // Dialog
        dialog(Screen.ConfirmDialog.route) {
            val context = LocalContext.current
            ConfirmDialog(
                showDialog = true,
                onConfirm = {
                    Toast.makeText(context, "Confirmed!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
