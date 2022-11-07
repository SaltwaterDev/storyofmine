package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.story.CommentRepository
import com.unlone.app.data.story.StoryRepository
import com.unlone.app.data.story.StoryResult
import com.unlone.app.domain.entities.Comment
import com.unlone.app.domain.entities.NetworkState
import com.unlone.app.domain.useCases.CheckNetworkStateUseCase
import com.unlone.app.domain.useCases.stories.FetchStoryDetailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class StoryDetailUiState(
    val pid: String = "",
    val title: String = "\b".repeat(5),
    val content: String = "\b".repeat(10),
    val authorId: String = "",
    val topic: String = "\b".repeat(5),
    val createdDate: String = "",
    val comments: List<Comment> = listOf(),
    val isSelfWritten: Boolean = false,
    val isSaved: Boolean = false,
    val allowComment: Boolean = false,
    val allowSave: Boolean = false,
    val errorMsg: String? = null,
    val loading: Boolean = false,
    val postCommentLoading: Boolean = false,
    val commentText: String = "",
    val networkState: NetworkState = NetworkState.Ok,
    val postCommentSucceed: Boolean = false,
)

class StoryDetailViewModel(
    private val fetchStoryDetailUseCase: FetchStoryDetailUseCase,
    private val commentRepository: CommentRepository,
    private val storyRepository: StoryRepository,
    private val checkNetworkStateUseCase: CheckNetworkStateUseCase,
) : ViewModel() {

    var state = MutableStateFlow(StoryDetailUiState())
        private set

    fun getStoryDetail(storyId: String) = viewModelScope.launch(Dispatchers.Default) {

        state.value = state.value.copy(loading = true)

        // check network state. Proceed if ok
        checkNetworkStateUseCase().apply {
            state.value = state.value.copy(networkState = this)
            when (this) {
                is NetworkState.Ok -> {}
                is NetworkState.UnknownError -> {
                    state.value = state.value.copy(
                        errorMsg = this.message
                    )
                    cancel()
                    return@launch
                }
                is NetworkState.Unavailable -> {
                    cancel()
                    return@launch
                }
            }
        }

        when (val result = fetchStoryDetailUseCase(storyId)) {
            is StoryResult.Success -> {
                launch { getComments(storyId) }
                launch {
                    result.data?.let { story ->
                        state.value = state.value.copy(
                            pid = storyId,
                            title = story.title,
                            content = story.content,
                            authorId = story.author,
                            topic = story.topic,
                            createdDate = story.createdDate,
                            allowComment = story.commentAllowed,
                            allowSave = story.saveAllowed,
                            isSelfWritten = story.isSelfWritten,
                            isSaved = story.isSaved,
                        )
                    }
                }
            }
            else -> {
                state.value = state.value.copy(errorMsg = result.errorMsg)
            }
        }

        state.value = state.value.copy(loading = false)
    }


    fun dismissError() {
        state.value = state.value.copy(errorMsg = null)
    }

    private fun getComments(sid: String) {
        viewModelScope.launch(Dispatchers.Default) {
            state.value = state.value.copy(loading = true)
            when (val result = commentRepository.getComments(sid)) {
                // todo: convert to ui state class
                is StoryResult.Success -> result.data?.let {
                    state.value = state.value.copy(comments = it)
                }
                is StoryResult.Failed -> state.value = state.value.copy(errorMsg = result.errorMsg)
                is StoryResult.UnknownError -> state.value =
                    state.value.copy(errorMsg = result.errorMsg)
            }
            state.value = state.value.copy(loading = false)
        }
    }

    fun setCommentText(text: String) {
        state.value = state.value.copy(commentText = text)
    }

    fun postComment(sid: String) = viewModelScope.launch {
        if (state.value.commentText.isNotBlank()) {
            state.value = state.value.copy(postCommentLoading = true)
            when (val result = commentRepository.postComment(sid, state.value.commentText)) {
                is StoryResult.Success -> result.data?.let {
                    state.value =
                        state.value.copy(
                            comments = state.value.comments + it,
                            commentText = "",
                            postCommentSucceed = true
                        )
                }
                is StoryResult.Failed -> state.value = state.value.copy(errorMsg = result.errorMsg)
                is StoryResult.UnknownError -> state.value =
                    state.value.copy(errorMsg = "_Server Error")
            }
            state.value = state.value.copy(postCommentLoading = false)
        }
    }

    fun saveStory(storyId: String) = viewModelScope.launch(Dispatchers.Default) {
        val newState = !state.value.isSaved
        state.value = state.value.copy(
            isSaved = newState
        )
        when (val result = storyRepository.saveStory(storyId, newState)) {
            is StoryResult.Success -> {}
            is StoryResult.Failed -> {
                state.value = state.value.copy(
                    isSaved = !newState,
                    errorMsg = result.errorMsg
                )
            }
            is StoryResult.UnknownError -> {
                state.value = state.value.copy(
                    isSaved = !newState,
                    errorMsg = "Unknown error: ${result.errorMsg}"
                )
            }
        }
    }

    val dismissPostCommentSucceed = {
        state.value = state.value.copy(
            postCommentSucceed = false
        )
    }
}