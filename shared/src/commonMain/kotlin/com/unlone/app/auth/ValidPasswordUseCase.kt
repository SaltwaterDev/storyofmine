package com.unlone.app.auth

class ValidPasswordUseCase {
    operator fun invoke(password: String): Boolean {
        // more validation...
        return Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$").matches(password)
    }
}