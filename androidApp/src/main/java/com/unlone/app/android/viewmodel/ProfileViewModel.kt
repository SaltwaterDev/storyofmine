package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.android.model.AuthUiEvent
import com.unlone.app.auth.AuthRepository
import com.unlone.app.auth.AuthResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber


data class ProfileUiState(
    val isUserLoggedIn: Boolean = false,
    val errorMsg: String? = null,
    val loading: Boolean = true
) {
    val profileItemList: List<ProfileItemList> =
        listOf(
            ProfileItemList.Draft(),
            ProfileItemList.MyStories(),
            ProfileItemList.Saved(),
            ProfileItemList.Setting(),
            ProfileItemList.Help(),
            ProfileItemList.Logout(),
        )
}

sealed class ProfileItemList(
    open val name: String,
    open val requireLoggedIn: Boolean,
) {
    data class Draft(
        override val name: String = "Draft",
        override val requireLoggedIn: Boolean = false
    ) : ProfileItemList(name, requireLoggedIn)

    data class MyStories(
        override val name: String = "MyStories",
        override val requireLoggedIn: Boolean = false
    ) : ProfileItemList(name, requireLoggedIn)

    data class Saved(
        override val name: String = "Saved",
        override val requireLoggedIn: Boolean = false
    ) : ProfileItemList(name, requireLoggedIn)

    data class Setting(
        override val name: String = "Setting",
        override val requireLoggedIn: Boolean = false
    ) : ProfileItemList(name, requireLoggedIn)

    data class Help(
        override val name: String = "Help",
        override val requireLoggedIn: Boolean = false
    ) : ProfileItemList(name, requireLoggedIn)

    data class Logout(
        override val name: String = "Logout",
        override val requireLoggedIn: Boolean = true
    ) : ProfileItemList(name, requireLoggedIn)
}


class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    init {
        authenticate()
    }

    private fun authenticate() {
        _state.value = _state.value.copy(loading = true)
        viewModelScope.launch {
            when (authRepository.authenticate()) {
                is AuthResult.Authorized -> {
                    _state.value = _state.value.copy(isUserLoggedIn = true)
                }
                is AuthResult.Unauthorized -> {
                    _state.value = _state.value.copy(isUserLoggedIn = false)
                }
                is AuthResult.UnknownError -> {
                    _state.value = _state.value.copy(isUserLoggedIn = false)
                    _state.value = _state.value.copy(errorMsg = "An unknown error occurred")
                }
            }
            _state.value = _state.value.copy(loading = false)
        }
    }

    fun signOut() {
        if (authRepository.signOut())
            authenticate()
    }
}
