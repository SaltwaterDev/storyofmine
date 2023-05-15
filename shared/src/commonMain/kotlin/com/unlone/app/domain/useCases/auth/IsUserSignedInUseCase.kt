package com.unlone.app.domain.useCases.auth

import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class IsUserSignedInUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return authRepository.isUserSignedIn
    }
}