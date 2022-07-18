package com.unlone.app.domain.useCases.auth

import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult

class IsUserSignedInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return authRepository.authenticate() is AuthResult.Authorized
    }
}