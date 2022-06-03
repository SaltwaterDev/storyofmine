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

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val btnEnabled: Boolean = true,
    val userExists: Boolean = false
)

class SignUpViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(SignUpUiState())
        private set

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.SignUpUsernameChanged -> {
                uiState = uiState.copy(email = event.value)
            }
            is AuthUiEvent.SignUpPasswordChanged -> {
                uiState = uiState.copy(password = event.value)
            }
            is AuthUiEvent.SignUp -> {
                signUp()
            }
            else -> {}

        }
    }

    private fun signUp() {
        uiState = uiState.copy(loading = true, btnEnabled = false)
        viewModelScope.launch {
            val result = authRepository.signUp(
                username = uiState.email,
                password = uiState.password,
            )
            resultChannel.send(result)
            uiState = uiState.copy(loading = false, btnEnabled = true)
        }
    }

    fun dismissMsg() {
        uiState = uiState.copy(
            errorMsg = null
        )
    }
}