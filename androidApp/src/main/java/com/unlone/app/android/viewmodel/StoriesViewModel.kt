package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.domain.entities.StoryItem
import com.unlone.app.domain.useCases.stories.FetchStoryItemsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StoriesScreenUiState(
    val loading: Boolean = true,
    val isUserLoggedIn: Boolean = true,
    val isRefreshing: Boolean = false,
    val storiesByTopics: List<StoryItem.StoriesByTopic>? = listOf(
        StoryItem.StoriesByTopic(),
        StoryItem.StoriesByTopic()
    ),
    val errorMsg: String? = null,
    val lastItemId: String? = null,
    val username: String? = null,
)


class StoriesViewModel(
    private val authRepository: AuthRepository,
    private val fetchStoryItemsUseCase: FetchStoryItemsUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<StoriesScreenUiState> =
        MutableStateFlow(StoriesScreenUiState())
    val state = _state.asStateFlow()

    val storiesByTopics = fetchStoryItemsUseCase()

    init {
        viewModelScope.launch {
            initData()
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRefreshing = true)
            withContext(Dispatchers.Default) { initData() }
            _state.value = _state.value.copy(isRefreshing = false)

        }
    }

    suspend fun initData() {
        _state.value = _state.value.copy(loading = true)
        if (authRepository.authenticate() is AuthResult.Authorized) {
            getUserName()
            _state.value = _state.value.copy(
                isUserLoggedIn = true,
            )
        }
        _state.value = _state.value.copy(loading = false)
    }

    private suspend fun getUserName() {
        when (val getUsernameResponse = authRepository.getUsername()) {
            // getUsername
            is AuthResult.Authorized -> getUsernameResponse.data?.let {
                _state.value = _state.value.copy(username = it)
            }
            else -> _state.value =
                _state.value.copy(errorMsg = getUsernameResponse.errorMsg)
        }
    }

    fun dismissError() {
        _state.value = _state.value.copy(errorMsg = null)
    }

    fun checkAuth() {
        viewModelScope.launch {
            _state.value = when (val authResult = authRepository.authenticate()) {
                is AuthResult.Authorized -> _state.value.copy(
                    isUserLoggedIn = true,
                )
                is AuthResult.Unauthorized -> _state.value.copy(
                    isUserLoggedIn = false,
                )
                is AuthResult.UnknownError -> _state.value.copy(
                    errorMsg = "Unknown error: " + authResult.errorMsg
                )
            }
        }
    }
}