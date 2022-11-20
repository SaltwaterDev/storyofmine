package com.unlone.app.android.viewmodel

import android.os.Parcelable
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
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
) : ViewModel() {

    var state = isUserSignedInUseCase().combine(authRepository.username) { isSignIn, username ->

//        var error: String? = null
        /*when (val getUsernameResponse = authRepository.username) {
            is AuthResult.Authorized -> getUsernameResponse.data?.let { name ->
                username =  authRepository.username
            }
            else -> error = getUsernameResponse.errorMsg
        }*/

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
