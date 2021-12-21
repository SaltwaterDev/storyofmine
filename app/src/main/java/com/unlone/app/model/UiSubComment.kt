package com.unlone.app.model

import androidx.annotation.Keep

@Keep
data class UiSubComment(
    val subComment: SubComment,
    val likedByUser: Boolean
)
