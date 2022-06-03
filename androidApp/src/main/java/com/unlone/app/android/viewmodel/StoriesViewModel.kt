package com.unlone.app.android.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.auth.AuthRepository
import com.unlone.app.auth.AuthResult
import com.unlone.app.model.PostsByTopic
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class LoungeUiState(
    val isUserLoggedIn: Boolean = false,
    val postsByTopics: List<PostsByTopic>? = null,
    val errorMsg: String? = null,
)


class StoriesViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoungeUiState())
    val state = _state.asStateFlow()

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    init {
        authenticate()

        viewModelScope.launch {
            authResult.collect { result ->
                when (result) {
                    is AuthResult.Authorized -> {
                        _state.value = _state.value.copy(isUserLoggedIn = true)
                    }
                    is AuthResult.Unauthorized -> {
                        _state.value = _state.value.copy(isUserLoggedIn = false)
                        Timber.d("User is not authorized")
                    }
                    is AuthResult.UnknownError -> {
                        _state.value = _state.value.copy(errorMsg = "An unknown error occurred")
                    }
                }
            }

        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            val result = authRepository.authenticate()
            resultChannel.send(result)
        }
    }

    fun dismissError() {
        _state.value = _state.value.copy(errorMsg = null)
    }
}

fun getPosts(): List<PostsByTopic> {
    return listOf(
        PostsByTopic.mock(),
        PostsByTopic.mock(),
        PostsByTopic.mock(),
        PostsByTopic.mock()
    )
}
