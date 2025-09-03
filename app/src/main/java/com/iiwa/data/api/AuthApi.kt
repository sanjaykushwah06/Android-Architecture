/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Authentication API interface defining login, signup, and password recovery endpoints
 */

package com.iiwa.data.api

import com.iiwa.data.model.ForgotPasswordRequest
import com.iiwa.data.model.ForgotPasswordResponse
import com.iiwa.data.model.LoginRequest
import com.iiwa.data.model.LoginResponse
import com.iiwa.data.model.VerifyOtpRequest
import com.iiwa.data.model.VerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): Response<VerifyOtpResponse>
}










