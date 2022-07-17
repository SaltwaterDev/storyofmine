package com.unlone.app.android.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state = MutableStateFlow(PostDetailUiState())

    private val pid = savedStateHandle.get<String>("pid")
    init {
        Log.d(TAG, "postDetailViewModel: $pid")
    }

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