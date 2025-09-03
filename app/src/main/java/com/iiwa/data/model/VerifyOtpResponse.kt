package com.iiwa.data.model
data class VerifyOtpResponse(
    val message: String,
    val success: Boolean,
    val token: String? = null
)
