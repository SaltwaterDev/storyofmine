package com.example.app.data.repo.mockObjects

import com.example.app.data.auth.AuthRepository
import com.example.app.data.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

internal class MockAuthRepository : AuthRepository {
    override val isUserSignedIn: Flow<Boolean>
        get() = flowOf(true)
    override var username: MutableStateFlow<String?>
        get() = TODO("Not yet implemented")
        set(value) {}

    override suspend fun signUpEmail(email: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(email: String, password: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signInEmail(email: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signIn(email: String, password: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun requestOtpEmail(email: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun verifyOtp(email: String, otp: Int): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }

    override fun getJwt(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun setUserName(email: String, username: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun removeUserRecordByEmail(email: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

}