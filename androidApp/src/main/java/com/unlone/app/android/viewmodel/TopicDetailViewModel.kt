package com.unlone.app.android.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.story.SimpleStory
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.useCases.stories.FetchStoriesByTopicUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TopicStoriesUiState(
    val topic: String? = null,
    val createdDate: String = "",
    val stories: List<SimpleStory>? = listOf(SimpleStory.mock()),
    val errorMsg: String? = null,
    val lastPage: Int? = null,
    val isFollowing: Boolean = false,
    val loading: Boolean = false,
    val isRefreshing: Boolean = false
)


class TopicDetailViewModel(
    private val fetchStoriesByTopicUseCase: FetchStoriesByTopicUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<TopicStoriesUiState> =
        MutableStateFlow(TopicStoriesUiState())
    val state = _state.asStateFlow()

    suspend fun initData(topic: String) = withContext(Dispatchers.Default) {
        _state.value = _state.value.copy(
            topic = topic,
            loading = true
        )
        when (val result = fetchStoriesByTopicUseCase(topic, state.value.lastPage)) {
            is StoryResult.Success -> {
                _state.value = _state.value.copy(
                    stories = result.data,
                    loading = false,
                )
            }
            is StoryResult.Failed -> {
                _state.value = _state.value.copy(
                    errorMsg = result.errorMsg,
                    loading = false,
                )
                Log.e("TAG", "initData: ${result.errorMsg}")
            }
            is StoryResult.UnknownError -> {
                _state.value = _state.value.copy(
                    errorMsg = result.errorMsg,
                    loading = false,
                )
                Log.e("TAG", "initData: ${result.errorMsg}")
            }
        }
    }

    fun toggleFollowing() {
        // todo: update following state to db
        _state.value = _state.value.copy(
            isFollowing = !_state.value.isFollowing
        )
    }

    suspend fun refresh(topic: String) {
        _state.value = _state.value.copy(
            isRefreshing = true
        )
        initData(topic)
        _state.value = _state.value.copy(
            isRefreshing = false
        )
    }
}