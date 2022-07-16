package com.unlone.app.data.auth

class IsUserSignedInUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.getJwt() != null
    }
}