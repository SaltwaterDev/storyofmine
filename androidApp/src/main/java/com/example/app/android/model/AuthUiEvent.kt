package com.example.app.android.model

sealed class SignUpUiEvent {
    data class SignUpEmailChanged(val value: String) : SignUpUiEvent()
    data class SignUpPasswordChanged(val value: String) : SignUpUiEvent()
    data class ConfirmedPasswordChanged(val value: String) : SignUpUiEvent()
    data class UsernameChanged(val value: String) : SignUpUiEvent()
    object SignUpEmailVerify : SignUpUiEvent()
    object SignUp : SignUpUiEvent()
    object SetUsername : SignUpUiEvent()
}

sealed class SignInUiEvent {
    data class SignInEmailChanged(val value: String) : SignInUiEvent()
    data class SignInPasswordChanged(val value: String) : SignInUiEvent()
    object SignInEmail : SignInUiEvent()
    object SignInPw : SignInUiEvent()
}