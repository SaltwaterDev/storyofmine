package com.unlone.app.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class  TokenResponse(
    val token: String
)