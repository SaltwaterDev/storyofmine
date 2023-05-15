package com.example.app.android.model

import com.example.app.domain.entities.Comment

data class StoryItemUiState(
    val title: String,
    var imagePath: String = "",
    val content: String,
    val pid: String,
    val firstComment: Comment? = null
)