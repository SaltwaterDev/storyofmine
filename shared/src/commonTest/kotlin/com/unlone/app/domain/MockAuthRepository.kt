package com.unlone.app.domain

import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.data.write.DraftRepository
import com.unlone.app.domain.entities.Draft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class MockAuthRepository : AuthRepository {

    override val isUserSignedIn: Flow<Boolean>
        get() = flowOf(true)
    override var username: MutableStateFlow<String?>
        get() = MutableStateFlow("user123")
        set(value) {}

    override suspend fun signUpEmail(email: String): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun signUp(email: String, password: String): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun signInEmail(email: String): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun signIn(email: String, password: String): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun requestOtpEmail(email: String): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun verifyOtp(email: String, otp: Int): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun signOut() {

    }

    override fun getJwt(): String {
        return "jwt1234"
    }

    override suspend fun setUserName(email: String, username: String): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

    override suspend fun removeUserRecordByEmail(email: String): AuthResult<Unit> {
        return AuthResult.Authorized()
    }

}