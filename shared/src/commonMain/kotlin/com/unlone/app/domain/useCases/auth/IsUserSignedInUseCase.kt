package com.unlone.app.domain.useCases.auth

import com.unlone.app.data.auth.AuthRepository

class IsUserSignedInUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.getJwt() != null
    }
}