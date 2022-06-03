package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow


data class ProfileUiState(
    val isUserLoggedIn: Boolean = false,
    val profileItemList: List<ProfileItemList> = emptyList()
)

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
//    val authRepository: AuthRepository
) : ViewModel() {

    val state = MutableStateFlow(ProfileUiState())

/*
    val state = authRepository.isUserLoggedIn.map {

        Timber.d("" + it)

        ProfileUiState(
            isUserLoggedIn = it,
            profileItemList = listOf(
                ProfileItemList.Draft(),
                ProfileItemList.MyStories(),
                ProfileItemList.Saved(),
                ProfileItemList.Setting(),
                ProfileItemList.Help(),
                ProfileItemList.Logout(),
            )
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        ProfileUiState()
    )

    fun logout() {
        authRepository.logout()
    }*/
}