package com.unlone.app.data.repo

import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class TestAuthRepository : AuthRepository {
    override val isUserSignedIn: Flow<Boolean>
        get() = TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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