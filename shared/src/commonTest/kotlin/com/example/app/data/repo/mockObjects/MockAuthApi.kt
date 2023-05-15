package com.example.app.data.repo.mockObjects

import com.example.app.data.api.AuthApi
import com.example.app.data.auth.*

class MockAuthApi: AuthApi {
    override suspend fun signUp(request: AuthRequest) {
    }

    override suspend fun checkEmailExisted(request: AuthEmailRequest) {

    }

    override suspend fun signIn(request: AuthRequest): TokenResponse {
        return TokenResponse("token")
    }

    override suspend fun validateEmail(request: AuthEmailRequest) {

    }

    override suspend fun authenticate(token: String): UserResponse {
        return UserResponse("id", "username")
    }

    override suspend fun requestOtp(email: String) {

    }

    override suspend fun verifyOtp(request: AuthOtpRequest) {
    }

    override suspend fun setUserName(email: String, username: String) {
    }

    override suspend fun getUserName(token: String): String {
        return "username"
    }

    override suspend fun removeUserRecord(email: String) {
    }

}