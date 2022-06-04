package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.model.AuthUiEvent
import com.unlone.app.auth.AuthRepository
import com.unlone.app.auth.AuthResult
import com.unlone.app.auth.ValidPasswordUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class SignUpUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val pwError: Boolean = false,
    val errorMsg: String? = null,
    val loading: Boolean = false,
) {
    val btnEnabled: Boolean = !loading && email.isNotBlank() && password.isNotBlank()
}

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val validatePasswordUseCase: ValidPasswordUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(SignUpUiState())
        private set

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.SignUpEmailChanged -> {
                uiState = uiState.copy(email = event.value)
            }
            is AuthUiEvent.SignUpPasswordChanged -> {
                uiState = uiState.copy(password = event.value)
            }
            is AuthUiEvent.SignUp -> {
                if (validatePasswordUseCase(uiState.password))
                    signUp()
                else
                    uiState = uiState.copy(pwError = true)
            }
            else -> {}

        }
    }

    private fun signUp() {
        uiState = uiState.copy(loading = true)
        viewModelScope.launch {
            val result = authRepository.signUp(
                email = uiState.email,
                password = uiState.password,
                username = uiState.username,
            )
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