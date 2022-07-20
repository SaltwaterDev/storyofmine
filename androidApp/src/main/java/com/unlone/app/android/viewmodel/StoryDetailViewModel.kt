package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.domain.useCases.stories.FetchStoryDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class StoryDetailUiState(
    val pid: String = "",
    val title: String = "Title",
    val content: String = "Content",
    val authorId: String = "",
    val topic: String = "Topic",
    val timestamp: Long = 0L,
    val comments: List<String> = emptyList(),
    val isSelfWritten: Boolean = false,
    val allowComment: Boolean = false,
    val allowSave: Boolean = false,
)

class StoryDetailViewModel(
    private val fetchStoryDetailUseCase: FetchStoryDetailUseCase
) : ViewModel() {

    fun getStoryDetail(postId: String) {
        viewModelScope.launch {
            val story = fetchStoryDetailUseCase(postId)
            state.value = state.value.copy(
                pid = postId,
                title = story.title,
                content = story.content,
                authorId = story.author,
                topic = story.topic,
                timestamp = story.timestamp,
                allowComment = story.commentAllowed,
                allowSave = story.saveAllowed,
            )
        }
    }

    //    private val pid: String? = savedStateHandle["pid"]
    var state = MutableStateFlow(StoryDetailUiState())
        private set

/*
    var state = pid?.let { pid ->
        PostDetailUiState(
            pid = pid,
            title = "Title",
            content = "content...",
            uid = "12345",
            topics = listOf("abc"),
            timestamp = 1653228333,
            allowComment = true,
            allowSave = true,
        )
    }
*/
}