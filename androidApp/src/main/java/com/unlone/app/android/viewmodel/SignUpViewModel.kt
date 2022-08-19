package com.unlone.app.android.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val verified: Boolean = false,
    val otp: String = "",
    val success: Boolean = false,
) {
    val btnEnabled: Boolean = !loading &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            password == confirmedPassword &&
            !emailError &&
            !pwError
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
            when (val result =
                authRepository.setUserName(email = uiState.email, username = uiState.username)) {
                is AuthResult.Authorized -> {
                    signIn()
                }
                is AuthResult.Unauthorized -> {
                    uiState = uiState.copy(errorMsg = result.errorMsg)
                }
                is AuthResult.UnknownError -> {
                    uiState = uiState.copy(errorMsg = "unknown error: " + result.errorMsg)
                }
            }
            uiState = uiState.copy(loading = false)
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            uiState = when (val result = authRepository.signIn(uiState.email, uiState.password)) {
                is AuthResult.Authorized -> uiState.copy(success = true)
                is AuthResult.Unauthorized -> {
                    uiState.copy(errorMsg = result.errorMsg)
                }
                is AuthResult.UnknownError -> {
                    uiState.copy(errorMsg = "unknown error: " + result.errorMsg)
                }
            }
        }
    }

    fun dismissErrorMsg() {
        uiState = uiState.copy(
            errorMsg = null
        )
    }

    fun removeSignUpRecord() {
        viewModelScope.launch {
            when (val result = authRepository.removeUserRecordByEmail(uiState.email)) {
                is AuthResult.Authorized -> {
//                    Timber.d("signup record removed")
                    Log.d("Tag", "signup record removed")
                }
                is AuthResult.Unauthorized -> {
                    uiState = uiState.copy(errorMsg = result.errorMsg)
                }
                is AuthResult.UnknownError -> {
                    uiState = uiState.copy(errorMsg = "unknown error: " + result.errorMsg)
                }
            }
        }
    }

    fun verifyOtp() {
        uiState = uiState.copy(loading = true)
        viewModelScope.launch {
            when (val result = uiState.otp.toIntOrNull()?.let {
                Log.d("TAG", "verifyOtp: $it")
                authRepository.verifyOtp(uiState.email, it)
            }) {
                is AuthResult.Authorized -> {
                    uiState = uiState.copy(verified = true)
                    Timber.d("email validate")
                }
                is AuthResult.Unauthorized -> {
                    uiState = uiState.copy(errorMsg = result.errorMsg)
                }
                is AuthResult.UnknownError -> {
                    uiState = uiState.copy(errorMsg = "unknown error: " + result.errorMsg)
                }
                else -> {}
            }
            uiState = uiState.copy(loading = false)
        }
    }

    fun generateOtp() {
        viewModelScope.launch {
            authRepository.requestOtpEmail(uiState.email)
        }
    }

    val setOtp: (String) -> Unit =
        { otp: String -> uiState = uiState.copy(otp = otp)}
}