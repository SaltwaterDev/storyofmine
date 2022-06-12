package com.unlone.app.auth

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
