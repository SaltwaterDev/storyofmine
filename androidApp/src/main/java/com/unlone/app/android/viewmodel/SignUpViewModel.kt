package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.model.SignUpUiEvent
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.domain.useCases.auth.ValidPasswordUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val confirmedPassword: String = "",
    val emailError: Boolean = false,
    val errorMsg: String? = null,
    val pwError: Boolean = false,
    val loading: Boolean = false,
    val succeed: Boolean = false,
) {
    val btnEnabled: Boolean = !loading &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            password == confirmedPassword
    val confirmedPwError: Boolean =
        confirmedPassword.isNotBlank() && (password != confirmedPassword)
}

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val validatePasswordUseCase: ValidPasswordUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(SignUpUiState())
        private set
    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.SignUpEmailChanged -> {
                uiState = uiState.copy(email = event.value)
            }
            is SignUpUiEvent.SignUpPasswordChanged -> {
                uiState = uiState.copy(password = event.value)
                validatePassword()
            }
            is SignUpUiEvent.ConfirmedPasswordChanged -> {
                uiState = uiState.copy(confirmedPassword = event.value)
            }
            is SignUpUiEvent.SignUpEmailVerify -> {
                signUpEmailVerify()
            }
            is SignUpUiEvent.SignUp -> {
                if (!uiState.pwError)
                    signUp()
            }
            is SignUpUiEvent.UsernameChanged -> {
                uiState = uiState.copy(username = event.value)
            }
            SignUpUiEvent.SetUsername -> {
                setUsername()
            }
        }
    }


    private fun validatePassword() {
        uiState = uiState.copy(pwError = !validatePasswordUseCase(uiState.password))
    }

    private fun signUpEmailVerify() {
        uiState = uiState.copy(loading = true)
        viewModelScope.launch {
            val result = authRepository.signUpEmail(
                email = uiState.email
            )
            when (result) {
                is AuthResult.Authorized -> {
                    uiState = uiState.copy(emailError = false)
                    Timber.d("email validate")
                }
                is AuthResult.Unauthorized -> {
                    uiState = uiState.copy(emailError = true)
                }
                is AuthResult.UnknownError -> {
                    uiState = uiState.copy(errorMsg = "unknown error: " + result.errorMsg)
                }
            }
            uiState = uiState.copy(loading = false)
        }
    }

    private fun signUp() {
        uiState = uiState.copy(loading = true)
        viewModelScope.launch {
            val result = authRepository.signUp(
                email = uiState.email,
                password = uiState.password,
            )
            resultChannel.send(result)
            uiState = uiState.copy(loading = false)
        }
    }

    private fun setUsername() {
        uiState = uiState.copy(loading = true)
        viewModelScope.launch {
            val result =
                authRepository.setUserName(email = uiState.email, username = uiState.username)
            resultChannel.send(result)
            uiState = uiState.copy(loading = false)
        }
    }

    fun dismissMsg() {
        uiState = uiState.copy(
            errorMsg = null
        )
    }
}