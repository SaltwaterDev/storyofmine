package com.example.app.data.auth

@kotlinx.serialization.Serializable
data class UserResponse(
    val id: String,
    val username: String,
)
