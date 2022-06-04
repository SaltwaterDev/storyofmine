package com.unlone.app.android.model

sealed class AuthUiEvent {
    data class SignUpEmailChanged(val value: String) : AuthUiEvent()
    data class SignUpPasswordChanged(val value: String) : AuthUiEvent()
    data class ConfirmedPasswordChanged(val value: String) : AuthUiEvent()
    object SignUpEmailVerify : AuthUiEvent()
    object SignUp : AuthUiEvent()

    data class SignInEmailChanged(val value: String) : AuthUiEvent()
    data class SignInPasswordChanged(val value: String) : AuthUiEvent()
    object SignIn : AuthUiEvent()
}