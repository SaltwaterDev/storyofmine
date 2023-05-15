package com.example.app.model

import androidx.annotation.Keep

@Keep
data class SubCommentUiState(
    val subComment: SubComment,
    val likedByUser: Boolean
)
