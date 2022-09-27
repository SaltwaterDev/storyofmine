package com.unlone.app.android.model

import com.unlone.app.domain.entities.Comment

data class StoryItemUiState(
    val title: String,
    var imagePath: String = "",
    val content: String,
    val pid: String,
    val firstComment: Comment? = null
)