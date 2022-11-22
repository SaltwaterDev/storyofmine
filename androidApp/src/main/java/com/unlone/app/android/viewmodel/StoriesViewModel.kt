package com.unlone.app.android.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.story.TopicRepository
import com.unlone.app.domain.entities.NetworkState
import com.unlone.app.domain.entities.StoryItem
import com.unlone.app.domain.useCases.CheckNetworkStateUseCase
import com.unlone.app.domain.useCases.stories.FetchStoryItemsUseCase
import com.unlone.app.domain.useCases.stories.GetTopicStoriesForRequestedStoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
    val networkState: NetworkState = NetworkState.Ok,
)


class StoriesViewModel(
    private val authRepository: AuthRepository,
    private val checkNetworkStateUseCase: CheckNetworkStateUseCase,
    fetchStoryItemsUseCase: FetchStoryItemsUseCase,
    private val getTopicStoriesForRequestedStoryUseCase: GetTopicStoriesForRequestedStoryUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state: MutableStateFlow<StoriesScreenUiState> =
        MutableStateFlow(StoriesScreenUiState())
    val state = _state.combine(authRepository.username) { state, username ->
        state.copy(username = username)
    }.stateIn(viewModelScope, SharingStarted.Lazily, StoriesScreenUiState())

    val storiesByTopics = fetchStoryItemsUseCase().cachedIn(viewModelScope)
    var storiesFromRequest = MutableStateFlow<StoryItem.StoriesByTopic?>(null)
        private set


    init {
        viewModelScope.launch {
            initData()
        }
    }

    suspend fun initData() = withContext(Dispatchers.Default) {
        _state.value = _state.value.copy(loading = true)
        // check network state. Proceed if ok
        checkNetworkStateUseCase().apply {
            _state.value = _state.value.copy(networkState = this)
            when (this) {
                is NetworkState.Ok -> {
                    if (authRepository.authenticate() is AuthResult.Authorized) {
                        _state.value = _state.value.copy(
                            isUserLoggedIn = true,
                        )
                    }
                }
                is NetworkState.UnknownError -> {
                    _state.value = _state.value.copy(
                        errorMsg = this.message
                    )
                }
                is NetworkState.Unavailable -> {
                    /*do nothing, as already update the state*/
                }
            }
        }

        _state.value = _state.value.copy(loading = false)
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

    fun loadStoriesFromRequest(requestStory: String) {
        viewModelScope.launch {
            when (val result = getTopicStoriesForRequestedStoryUseCase(requestStory)) {
                is StoryResult.Success -> {
                    result.data?.let { storiesFromRequest.value = it }
                }
                is StoryResult.Failed -> {
                    _state.value = _state.value.copy(errorMsg = result.errorMsg)
                }
                is StoryResult.UnknownError -> {
                    _state.value = _state.value.copy(errorMsg = result.errorMsg)
                }
            }
        }
    }
}