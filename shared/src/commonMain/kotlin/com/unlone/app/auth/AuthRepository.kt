package com.unlone.app.auth

interface AuthRepository {
    suspend fun signUp(email: String, username: String, password: String): AuthResult<Unit>
    suspend fun signIn(email: String, password: String): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>
}