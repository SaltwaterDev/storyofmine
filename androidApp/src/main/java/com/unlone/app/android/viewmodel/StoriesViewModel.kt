package com.unlone.app.android.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.domain.entities.StoryItem
import com.unlone.app.domain.useCases.auth.IsUserSignedInUseCase
import com.unlone.app.domain.useCases.stories.FetchStoryItemsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StoriesScreenUiState(
    val loading: Boolean = true,
    val isUserLoggedIn: Boolean = true,
    val isRefreshing: Boolean = false,
    val storiesByTopics: List<StoryItem.StoriesByTopic>? = List(4) { StoryItem.StoriesByTopic() },
    val errorMsg: String? = null,
    val lastItemId: String? = null,
    val username: String? = null,
)


class StoriesViewModel(
    private val authRepository: AuthRepository,
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
    private val fetchStoryItemsUseCase: FetchStoryItemsUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<StoriesScreenUiState> =
        MutableStateFlow(StoriesScreenUiState())
    val state = _state.combine(authRepository.username) { state, username ->
        state.copy(username = username)
    }.stateIn(viewModelScope, SharingStarted.Lazily, StoriesScreenUiState())

    private val _storiesByTopics = fetchStoryItemsUseCase().cachedIn(viewModelScope)

    val storiesByTopics
        @Composable get() = _storiesByTopics.collectAsLazyPagingItems()


    init {
        viewModelScope.launch { initState() }
    }

    private suspend fun initState() = withContext(Dispatchers.Default) {
        _state.value = _state.value.copy(loading = true)
        checkAuth()
        _state.value = _state.value.copy(loading = false)
    }

    fun dismissError() {
        _state.value = _state.value.copy(errorMsg = null)
    }

    fun checkAuth() {
        viewModelScope.launch {
            isUserSignedInUseCase().catch { e ->
                _state.value = _state.value.copy(errorMsg = e.message)
            }.collect {
                _state.value = _state.value.copy(isUserLoggedIn = it)
            }
        }
    }
}