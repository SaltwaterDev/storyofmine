package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.data.userPreference.UserPreferenceRepository
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class ProfileUiState(
    val isUserLoggedIn: Boolean = false,
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val username: String = ""
)


class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
) : ViewModel() {

    var state = isUserSignedInUseCase().map {
        var username = ""
        var error: String? = null
        when (val getUsernameResponse = authRepository.getUsername()) {
            is AuthResult.Authorized -> getUsernameResponse.data?.let { name ->
                username = name
            }
            else -> error = getUsernameResponse.errorMsg
        }

        ProfileUiState(
            isUserLoggedIn = it,
            username = username,
            errorMsg = error
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState())


    fun signOut() = viewModelScope.launch(Dispatchers.Default) {
        authRepository.signOut()
    }
}
