package com.unlone.app.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.unlone.app.android.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


data class LoginUiState(
    val email: String? = null,
    val password: String? = null,
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val btnEnabled: Boolean = true,
    val userExists: Boolean = false
)


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set


    private fun isUserExisted(uid: String) {
        // TODO
    }

    fun performLogin(email: String, password: String) {
        authRepository.signIn()
    }

    fun dismissMsg() {
        uiState = uiState.copy(
            errorMsg = null
        )
    }
}