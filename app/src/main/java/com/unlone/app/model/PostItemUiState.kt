package com.unlone.app.model

import android.net.Uri

data class PostItemUiState(
    val title: String,
    var imagePath: String = "",
    val content: String,
    val pid: String,
    val firstComment: Comment? = null
)