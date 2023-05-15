package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import com.unlone.app.data.story.StoryResult
import com.unlone.app.data.story.Topic
import com.unlone.app.data.story.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow


data class FullTopicUiState(
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val topics: List<Topic> = listOf(),
)

class FullTopicViewModel(
    private val topicRepository: TopicRepository
) : ViewModel() {
    var uiState = MutableStateFlow(FullTopicUiState())
        private set

    suspend fun getAllTopic() {
        when (val result = topicRepository.getAllTopic()) {
            is StoryResult.Success -> {
                result.data?.let { uiState.value = uiState.value.copy(topics = it) }
            }
            is StoryResult.Failed -> {
                uiState.value = uiState.value.copy(errorMsg = result.errorMsg)
            }
            is StoryResult.UnknownError -> {
                uiState.value = uiState.value.copy(errorMsg = result.errorMsg)
            }
        }
    }
}