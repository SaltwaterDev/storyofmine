package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ProfileUiState(
    val isUserLoggedIn: Boolean = false,
    val errorMsg: String? = null,
    val loading: Boolean = true,
    val username: String = ""
)


class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    init {
        authenticate()
    }

    private fun authenticate() {
        _state.value = _state.value.copy(loading = true)
        viewModelScope.launch {
            when (authRepository.authenticate()) {
                is AuthResult.Authorized -> {
                    getUserName()
                    _state.value = _state.value.copy(isUserLoggedIn = true)
                }
                is AuthResult.Unauthorized -> {
                    _state.value = ProfileUiState()
                }
                is AuthResult.UnknownError -> {
                    _state.value = ProfileUiState()
                    _state.value = _state.value.copy(errorMsg = "An unknown error occurred")
                }
            }
            _state.value = _state.value.copy(loading = false)
        }
    }

    private fun getUserName() {
        viewModelScope.launch {
            when (val getUsernameResponse = authRepository.getUsername()) {
                // getUsername
                is AuthResult.Authorized -> getUsernameResponse.data?.let {
                    _state.value = _state.value.copy(username = it)
                }
                else -> _state.value =
                    _state.value.copy(errorMsg = getUsernameResponse.errorMsg)
            }
        }
    }


    fun signOut() {
        authRepository.signOut()
        authenticate()
    }
}
