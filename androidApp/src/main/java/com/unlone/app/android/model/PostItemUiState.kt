package com.unlone.app.model

data class PostItemUiState(
    val title: String,
    var imagePath: String = "",
    val content: String,
    val pid: String,
    val firstComment: Comment? = null
)