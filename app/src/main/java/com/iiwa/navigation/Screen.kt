/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Screen definitions and route constants for navigation
 */

package com.iiwa.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object ForgotPassword : Screen("forgot_password")
    object Otp : Screen("otp/{email}") {
        fun createRoute(email: String) = "otp/$email"
    }
    object ResetPassword : Screen("reset_password/{email}") {
        fun createRoute(email: String) = "reset_password/$email"
    }
    object Details : Screen("details/{itemId}") {
        fun createRoute(itemId: Int) = "details/$itemId"
    }
    object ConfirmDialog : Screen("confirm_dialog")
}
