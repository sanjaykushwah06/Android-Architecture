package com.iiwa.data.model

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)