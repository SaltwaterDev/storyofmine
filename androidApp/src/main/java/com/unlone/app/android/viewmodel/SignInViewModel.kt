package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.model.AuthUiEvent
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
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(SignInUiState())
        private set

    private val resultChannel = Channel<AuthResult<Unit>>()

    val authResult = resultChannel.receiveAsFlow()

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.SignInEmailChanged -> {
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
                email = uiState.email,
                password = uiState.password,
            )
            resultChannel.send(result)
        }
    }
}
