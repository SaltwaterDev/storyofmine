package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.model.SignInUiEvent
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
    val userExists: Boolean = false
) {
    val emailBtnEnabled: Boolean = email.isNotBlank() && !loading
    val pwBtnEnabled: Boolean = password.isNotBlank() && !loading

}

class SignInViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(SignInUiState())
        private set

    private val resultChannel = Channel<AuthResult<Unit>>()

    val authResult = resultChannel.receiveAsFlow()

    fun onEvent(event: SignInUiEvent) {
        when (event) {
            is SignInUiEvent.SignInEmailChanged -> {
                uiState = uiState.copy(email = event.value)
            }
            is SignInUiEvent.SignInPasswordChanged -> {
                uiState = uiState.copy(password = event.value)
            }
            is SignInUiEvent.SignInEmail -> {
                emailValidate()
            }
            is SignInUiEvent.SignInPw -> {
                signIn()
            }
        }
    }

    fun dismissMsg() {
        uiState = uiState.copy(
            errorMsg = null
        )
    }

    private fun emailValidate() {
        uiState = uiState.copy(loading = true)
        viewModelScope.launch {
            val result = authRepository.signInEmail(
                email = uiState.email
            )
            resultChannel.send(result)
            uiState = uiState.copy(loading = false)
        }
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
