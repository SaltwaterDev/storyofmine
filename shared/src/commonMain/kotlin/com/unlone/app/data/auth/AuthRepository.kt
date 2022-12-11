package com.unlone.app.data.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


interface AuthRepository {
    val isUserSignedIn: Flow<Boolean>
    var username: MutableStateFlow<String?>
    suspend fun signUpEmail(email: String): AuthResult<Unit>
    suspend fun signUp(email: String, password: String): AuthResult<Unit>
    suspend fun signInEmail(email: String): AuthResult<Unit>
    suspend fun signIn(email: String, password: String): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>
    suspend fun requestOtpEmail(email: String): AuthResult<Unit>
    suspend fun verifyOtp(email: String, otp: Int): AuthResult<Unit>
    suspend fun signOut()
    fun getJwt(): String?
    suspend fun setUserName(email: String, username: String): AuthResult<Unit>
    suspend fun removeUserRecordByEmail(email: String): AuthResult<Unit>
}