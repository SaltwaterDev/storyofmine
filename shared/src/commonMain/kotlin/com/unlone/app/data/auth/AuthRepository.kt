package com.unlone.app.data.auth


interface AuthRepository {
    suspend fun signUpEmail(email: String): AuthResult<Unit>
    suspend fun signUp(email: String, password: String): AuthResult<Unit>
    suspend fun signInEmail(email: String): AuthResult<Unit>
    suspend fun signIn(email: String, password: String): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>
    suspend fun requestOtpEmail(email: String): AuthResult<Unit>
    suspend fun verifyOtp(email: String, otp: Int): AuthResult<Unit>
    fun signOut()
    fun getJwt(): String?
    suspend fun setUserName(email: String, username: String): AuthResult<Unit>
    suspend fun getUsername(): AuthResult<String>
    suspend fun removeUserRecordByEmail(email: String): AuthResult<Unit>
}


class UserNotLoginException(message: String) : Exception(message)