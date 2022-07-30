package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.useCases.stories.FetchStoryDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

data class StoryDetailUiState(
    val pid: String = "",
    val title: String = "Title",
    val content: String = "Content",
    val authorId: String = "",
    val topic: String = "Topic",
    val createdDate: String = "",
    val comments: List<String> = listOf("comment", "comment", "comment", "comment", "comment", "comment"),
    val isSelfWritten: Boolean = false,
    val allowComment: Boolean = false,
    val allowSave: Boolean = false,
    val errorMsg: String? = null,
)

class StoryDetailViewModel(
    private val fetchStoryDetailUseCase: FetchStoryDetailUseCase,
) : ViewModel() {

    fun getStoryDetail(postId: String) {
        viewModelScope.launch {
            when (val result = fetchStoryDetailUseCase(postId)) {
                is StoryResult.Success -> {
                    result.data?.let { story ->
                        state.value = state.value.copy(
                            pid = postId,
                            title = story.title,
                            content = story.content,
                            authorId = story.author,
                            topic = story.topic,
                            createdDate = story.createdDate,
                            allowComment = story.commentAllowed,
                            allowSave = story.saveAllowed,
                            isSelfWritten = story.isSelfWritten,
                        )
                    }
                }
                else -> {
                    state.value = state.value.copy(errorMsg = result.errorMsg)
                }
            }
        }
    }

    var state = MutableStateFlow(StoryDetailUiState())
        private set

    fun dismissError() {
        state.value = state.value.copy(errorMsg = null)
    }
}