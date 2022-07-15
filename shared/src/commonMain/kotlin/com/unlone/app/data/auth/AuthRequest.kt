package com.unlone.app.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
)


@Serializable
data class AuthEmailRequest(
    val email: String
)

@Serializable
data class AuthOtpRequest(
    val email: String,
    val otp: Int,
)
