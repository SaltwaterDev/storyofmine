package com.unlone.app.domain.useCases

class ValidPasswordUseCase {
    operator fun invoke(password: String): Boolean {
        // more validation...
        return Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}\$").matches(password)
    }
}