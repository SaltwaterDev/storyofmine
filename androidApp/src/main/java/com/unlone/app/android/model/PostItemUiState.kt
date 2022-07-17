package com.unlone.app.model

import com.unlone.app.domain.entities.Comment

data class PostItemUiState(
    val title: String,
    var imagePath: String = "",
    val content: String,
    val pid: String,
    val firstComment: Comment? = null
)