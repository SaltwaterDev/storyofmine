package com.unlone.app.android.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.data.userPreference.UserPreferenceRepository
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileUiState(
    val isUserLoggedIn: Boolean = false,
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val username: String = ""
) : Parcelable


class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var state =
        authRepository.isUserSignedIn.combine(authRepository.username) { isSignIn, username ->

            ProfileUiState(
                isUserLoggedIn = isSignIn,
                username = username ?: "",
                errorMsg = if (username == null) "username is null" else null
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, ProfileUiState())


    fun signOut() = viewModelScope.launch(Dispatchers.Default) {
        authRepository.signOut()
    }

}
