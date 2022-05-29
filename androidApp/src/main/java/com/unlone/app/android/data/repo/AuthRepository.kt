package com.unlone.app.android.data.repo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthRepository @Inject constructor() {
    private val _isUserLoggedIn = MutableStateFlow(false)   // todo
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    fun logout() {
        // todo
        _isUserLoggedIn.value = false
    }

    fun signIn() {
        // todo
        _isUserLoggedIn.value = true
    }
}