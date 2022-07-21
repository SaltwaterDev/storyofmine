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
) {
    val profileItemList: List<ProfileItemList> =
        listOf(
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
