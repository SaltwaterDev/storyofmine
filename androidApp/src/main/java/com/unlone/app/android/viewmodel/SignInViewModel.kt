package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.auth.AuthRepository
import com.unlone.app.auth.AuthResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val btnEnabled: Boolean = true,
    val userExists: Boolean = false
)

class SignInViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(SignInUiState())
        private set

    private val resultChannel = Channel<AuthResult<Unit>>()

    val authResult = resultChannel.receiveAsFlow()

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.SignInUsernameChanged -> {
                uiState = uiState.copy(email = event.value)
            }
            is AuthUiEvent.SignInPasswordChanged -> {
                uiState = uiState.copy(password = event.value)
            }
            is AuthUiEvent.SignIn -> {
                signIn()
            }
            else -> {}
        }
    }

    fun dismissMsg() {
        uiState = uiState.copy(
            errorMsg = null
        )
    }

    private fun signIn() {
        viewModelScope.launch {
            val result = authRepository.signIn(
                username = uiState.email,
                password = uiState.password,
            )
            resultChannel.send(result)
        }
    }

}

sealed class AuthUiEvent {
    data class SignUpUsernameChanged(val value: String) : AuthUiEvent()
    data class SignUpPasswordChanged(val value: String) : AuthUiEvent()
    object SignUp : AuthUiEvent()

    data class SignInUsernameChanged(val value: String) : AuthUiEvent()
    data class SignInPasswordChanged(val value: String) : AuthUiEvent()
    object SignIn : AuthUiEvent()
}