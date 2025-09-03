/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - User repository for handling authentication and user data operations
 */

package com.iiwa.authorization.repository

import com.iiwa.data.api.AuthApi
import com.iiwa.data.local.TokenStorage
import com.iiwa.data.local.UserDao
import com.iiwa.data.model.ForgotPasswordResponse
import com.iiwa.data.model.LoginRequest
import com.iiwa.data.model.LoginResponse
import com.iiwa.data.model.User
import com.iiwa.data.model.VerifyOtpRequest
import com.iiwa.data.model.VerifyOtpResponse
import com.iiwa.data.remote.RemoteData
import com.iiwa.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
    private val remoteData: RemoteData,
    private val userDao: UserDao,
) {

    /**
     * Login user with email and password
     * Uses RemoteData for centralized network handling with crash analytics
     */
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        val loginRequest = LoginRequest(username = "emilys", password = "emilyspass")
        return when (val result = remoteData.executeCall { authApi.login(loginRequest) }) {
            is Result.Success -> {
                try {
                    // Validate response data
                    val validationResult = validateLoginResponse(result.data)
                    if (validationResult != null) {
                        return validationResult
                    }

                    // Store tokens and user info
                    tokenStorage.saveTokens(
                        accessToken = result.data.accessToken,
                        refreshToken = result.data.refreshToken,
                        userId = result.data.id,
                        username = result.data.username
                    )

                    // Insert user data into Room database
                    val user = User(
                        userId = result.data.id.toString(),
                        username = result.data.username,
                        token = result.data.accessToken
                    )
                    userDao.insertUser(user)

                    result
                } catch (e: Exception) {
                    // Log unexpected error during login processing
                    Result.Error("Login processing failed: ${e.message}")
                }
            }

            is Result.Error -> {
                // Log login failure
                result
            }
            
            is Result.Loading -> result
        }
    }

    /**
     * Validate login response data
     */
    private fun validateLoginResponse(response: LoginResponse): Result.Error? {
        return when {
            response.accessToken.isBlank() -> Result.Error("Invalid response: Missing access token")
            response.refreshToken.isBlank() -> Result.Error("Invalid response: Missing refresh token")
            response.id <= 0 -> Result.Error("Invalid response: Invalid user ID")
            response.username.isBlank() -> Result.Error("Invalid response: Missing username")
            response.email.isBlank() -> Result.Error("Invalid response: Missing email")
            else -> null
        }
    }


    /**
     * Logout user and clear stored data
     */
    suspend fun logout(): Result<Unit> {
        return try {
            val userId = tokenStorage.getUserId()
            val username = tokenStorage.getUsername()
            
            tokenStorage.clearTokens()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            // Log logout error
            Result.Error("Logout failed: ${e.message}")
        }
    }

    /**
     * Update access token (useful for token refresh)
     */
    suspend fun updateAccessToken(newAccessToken: String): Result<Unit> {
        return try {
            if (newAccessToken.isBlank()) {
                return Result.Error("Access token cannot be empty")
            }

            tokenStorage.updateAccessToken(newAccessToken)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update access token: ${e.message}")
        }
    }

    /**
     * Check if current tokens are valid
     */
    fun areTokensValid(): Boolean {
        val accessToken = tokenStorage.getAccessToken()
        val refreshToken = tokenStorage.getRefreshToken()

        return !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
    }

    /**
     * Get user authentication state as a Flow
     */
    fun getAuthState(): Flow<Boolean> = flow {
        emit(tokenStorage.isLoggedIn())
    }

    /**
     * Get current user info as a Flow
     */
    fun getCurrentUserInfo(): Flow<UserInfo?> = flow {
        if (tokenStorage.isLoggedIn()) {
            emit(
                UserInfo(
                    id = tokenStorage.getUserId(),
                    username = tokenStorage.getUsername() ?: "",
                    accessToken = tokenStorage.getAccessToken() ?: "",
                    refreshToken = tokenStorage.getRefreshToken() ?: ""
                )
            )
        } else {
            emit(null)
        }
    }

    /**
     * Send forgot password request
     * Uses RemoteData for centralized network handling
     */
    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> {
        // For now, return mock success since DummyJSON doesn't have forgot password endpoint
        // TODO: Replace with actual API call when backend is ready
        return Result.Success(
            ForgotPasswordResponse(
                message = "Password reset email sent successfully",
                success = true
            )
        )

        // When your backend has the forgot password endpoint, use this:
        /*
        val request = ForgotPasswordRequest(email = email)
        return when (val result = remoteData.executeCall { authApi.forgotPassword(request) }) {
            is Result.Success -> {
                // Validate response
                if (result.data.success) {
                    result
                } else {
                    Result.Error(result.data.message)
                }
            }
            is Result.Error -> result
            is Result.Loading -> result
        }
        */
    }

    /**
     * Verify OTP for password reset
     * Uses RemoteData for centralized network handling
     */
    suspend fun verifyOtp(email: String, otp: String): Result<VerifyOtpResponse> {
        val request = VerifyOtpRequest(email = email, otp = otp)
        
        return when (val result = remoteData.executeCall { authApi.verifyOtp(request) }) {
            is Result.Success -> {
                // Validate response
                if (result.data.success) {
                    result
                } else {
                    Result.Error(result.data.message)
                }
            }
            is Result.Error -> result
            is Result.Loading -> result
        }
    }
}

/**
 * Data class representing current user information
 */
data class UserInfo(
    val id: Int,
    val username: String,
    val accessToken: String,
    val refreshToken: String
)
