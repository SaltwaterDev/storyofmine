package com.unlone.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class PostDetailUiState(
    val pid: String = "",
    val title: String = "",
    val content: String = "",
    val uid: String = "",
    val topics: List<String> = emptyList(),
    val timestamp: Long = 0L,
    val comments: List<String> = emptyList(),
    val isSelfWritten: Boolean = false,
    val allowComment: Boolean = false,
    val allowSave: Boolean = false,
)

class PostDetailViewModel(
//    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state = MutableStateFlow(PostDetailUiState())

//    val pid = savedStateHandle.get<String>("pid")

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