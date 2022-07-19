package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class StoryDetailUiState(
    val pid: String = "",
    val title: String = "Title",
    val content: String = "Content",
    val authorId: String = "",
    val topics: List<String> = emptyList(),
    val timestamp: Long = 0L,
    val comments: List<String> = emptyList(),
    val isSelfWritten: Boolean = false,
    val allowComment: Boolean = false,
    val allowSave: Boolean = false,
)

class StoryDetailViewModel(
) : ViewModel() {

    fun getStoryDetail(postId: String) {
        /*TODO("Not yet implemented")*/
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