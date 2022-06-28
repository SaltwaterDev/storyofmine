package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.model.PostsByTopic
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoungeUiState(
    val loading: Boolean = true,
    val isUserLoggedIn: Boolean = false,
    val postsByTopics: List<PostsByTopic>? = null,
    val errorMsg: String? = null,
)


class StoriesViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state: MutableStateFlow<LoungeUiState> = MutableStateFlow(LoungeUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (authRepository.authenticate() is AuthResult.Authorized) {
                _state.value = _state.value.copy(isUserLoggedIn = true)
            }
            _state.value = _state.value.copy(loading = false)
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
