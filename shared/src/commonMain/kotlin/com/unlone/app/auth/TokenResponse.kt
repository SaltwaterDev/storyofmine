package com.unlone.app.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String
)